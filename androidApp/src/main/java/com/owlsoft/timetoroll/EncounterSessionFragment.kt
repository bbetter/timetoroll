package com.owlsoft.timetoroll

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.model.TickData
import com.owlsoft.shared.viewmodel.EncounterSessionViewModel
import com.owlsoft.timetoroll.databinding.EncounterSessionFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EncounterSessionFragment : Fragment(R.layout.encounter_session_fragment) {

    private lateinit var binding: EncounterSessionFragmentBinding

    private val adapter = EncounterSessionParticipantsAdapter()

    private val code by lazy { arguments?.getString("code") ?: "" }

    private var skipItem: MenuItem? = null
    private var editItem: MenuItem? = null

    private val encounterServiceIntent by lazy {
        Intent(requireContext(), EncounterService::class.java)
            .apply { putExtra("code", code) }
    }

    private val viewModel: EncounterSessionViewModel by viewModel(
        parameters = { parametersOf("code" to code) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (requireActivity() as MainActivity).hideKeyboard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EncounterSessionFragmentBinding.bind(view)

        with(binding) {
            setupToolbar()
            setupView()
        }
        setupSubscriptions()
    }

    private fun setupSubscriptions() {
        viewModel.data.watch {
            binding.updateTimer(it)
            binding.updatePlayPauseButton(it.isPlayPauseAllowed, it.isPaused)
            binding.updateRound(it.roundIndex)

            updateParticipantsList(it.participants, it.turnIndex)
            updateSkipTurnButton(it.isSkipTurnAllowed)
            updateEditEncounterButton(it.isPlayPauseAllowed)
        }
    }

    private fun EncounterSessionFragmentBinding.setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = "Encounter : $code"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.encounter_session_menu, menu)
        skipItem = menu.findItem(R.id.skipItem)
        editItem = menu.findItem(R.id.editItem)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.skipItem -> viewModel.skipTurn()
            R.id.editItem -> findNavController().navigate(
                R.id.action_encounterSessionFragment_to_encounterFragment,
                bundleOf("code" to code)
            )
            R.id.shareItem -> shareEncounter()
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        Log.d("lifecycle", "EncounterSessionFragment#onPause")
        requireContext().startService(encounterServiceIntent)
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifecycle", "EncounterSessionFragment#onResume")
        requireContext().stopService(encounterServiceIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("lifecycle", "EncounterSessionFragment#onDestroyView")
        requireContext().stopService(encounterServiceIntent)
    }

    private fun updateEditEncounterButton(isAdmin: Boolean) {
        editItem?.isVisible = isAdmin
    }

    private fun updateParticipantsList(participants: List<Participant>, turnIndex: Int) {
        adapter.updateParticipants(participants)
        adapter.updateActiveParticipant(turnIndex)
    }

    private fun updateSkipTurnButton(skipTurnAllowed: Boolean) {
        skipItem?.isVisible = skipTurnAllowed
    }

    private fun EncounterSessionFragmentBinding.updateRound(roundIndex: Int) {
        roundNumberTextView.text = getString(R.string.round_index, roundIndex)
    }

    private fun EncounterSessionFragmentBinding.updatePlayPauseButton(
        isPlayPauseAllowed: Boolean,
        isPaused: Boolean
    ) {
        if (isPlayPauseAllowed) {
            playPauseButton.visibility = View.VISIBLE
            val resource =
                if (!isPaused) R.drawable.ic_baseline_pause_circle_filled_24 else R.drawable.ic_baseline_play_arrow_24
            playPauseButton.setImageResource(resource)
        } else {
            playPauseButton.visibility = View.GONE
        }
    }

    private fun EncounterSessionFragmentBinding.updateTimer(tickData: TickData) {
        val colorRes = if (tickData.isInDangerZone) android.R.color.holo_red_light else R.color.primary_text
        val color = ContextCompat.getColor(requireContext(), colorRes)
        roundTimerView.setTextColor(color)
        roundTimerView.text = tickData.decoratedTick
    }

    private fun EncounterSessionFragmentBinding.setupView() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.close_confirmation_title))
                .setMessage(getString(R.string.close_confirmation_message))
                .setPositiveButton(getString(R.string.close_positive_button)) { d, _ ->
                    d.dismiss()
                    val navController = findNavController()
                    navController.popBackStack(R.id.menuFragment, false)
                }
                .setNegativeButton(getString(R.string.close_negative_button)) { d, _ ->
                    d.dismiss()
                }
                .create()
                .show()
        }

        playPauseButton.setOnClickListener {
            viewModel.doTrackerAction()
        }

        participantsList.adapter = adapter
        participantsList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun shareEncounter() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            //todo: share deep link (app link) when deployed to heroku
            putExtra(Intent.EXTRA_TEXT, code)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}