package com.owlsoft.turntoroll.encounter

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.owlsoft.shared.model.Participant
import com.owlsoft.turntoroll.EncounterService
import com.owlsoft.turntoroll.MainActivity
import com.owlsoft.turntoroll.R
import com.owlsoft.turntoroll.databinding.EncounterSessionFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class EncounterSessionFragment : Fragment(R.layout.encounter_session_fragment) {

    private lateinit var binding: EncounterSessionFragmentBinding

    private val timerFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val adapter = EncounterSessionParticipantsAdapter()

    private val code by lazy { arguments?.getString("code") ?: "" }

    private var skipItem: MenuItem? = null
    private var editItem: MenuItem? = null

    private val encounterService by lazy { Intent(requireContext(), EncounterService::class.java) }

    private val viewModel: EncounterSessionViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel.trackerLiveData.observe(viewLifecycleOwner) {
            binding.updateTimer(it.tick)
            binding.updatePlayPauseButton(it.isPlayPauseAllowed, it.isPaused)
            binding.updateRound(it.roundIndex)

            updateParticipantsList(it.participants, it.turnIndex)
            updateSkipTurnButton(it.isSkipTurnAllowed)
            updateEditEncounterButton(it.isPlayPauseAllowed)
        }
    }

    private fun updateEditEncounterButton(isAdmin: Boolean) {
        editItem?.isVisible = isAdmin
    }

    private fun updateParticipantsList(participants: List<Participant>, turnIndex: Int) {
        adapter.updateParticipants(participants)
        adapter.updateActiveParticipant(turnIndex)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = EncounterSessionFragmentBinding.bind(view)

        (requireActivity() as MainActivity).hideKeyboard()

        with(binding) {
            toolbar.title = "Encounter : $code"
            setupView()
        }

        startEncounterService()
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
        }
        return true
    }

    private fun startEncounterService() {
        val startIntent = encounterService.apply { putExtra("code", code) }
        requireContext().startService(startIntent)
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

    private fun EncounterSessionFragmentBinding.updateTimer(timerTick: Int) {
        val timer = timerFormatter.format(timerTick * 1000)
        val color = if (timerTick < 5) android.R.color.holo_red_light else R.color.primary_text
        roundTimerView.setTextColor(ContextCompat.getColor(requireContext(), color))
        roundTimerView.text = timer
    }

    private fun EncounterSessionFragmentBinding.setupView() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            val navController = findNavController()
            navController.popBackStack()
        }

        playPauseButton.setOnClickListener {
            val currentState = viewModel.trackerLiveData.value ?: return@setOnClickListener

            if (currentState.isPaused) {
                viewModel.resume()
            } else {
                viewModel.pause()
            }
        }

        participantsList.adapter = adapter
        participantsList.layoutManager = LinearLayoutManager(
            context,
            VERTICAL,
            false
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        requireContext().stopService(encounterService)
    }
}