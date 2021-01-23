package com.owlsoft.turntoroll.encounter

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.owlsoft.turntoroll.MainActivity
import com.owlsoft.turntoroll.R
import com.owlsoft.turntoroll.databinding.EncounterFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

class EncounterFragment : Fragment(R.layout.encounter_fragment) {

    private lateinit var binding: EncounterFragmentBinding

    private val adapter = EncounterParticipantsAdapter()

    private val timerFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val code by lazy { arguments?.getString("code") ?: "" }

    private var skipItem: MenuItem? = null

    private val viewModel: EncounterViewModel by viewModel {
        parametersOf(
            "code" to arguments?.getString(
                "code"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.track()

        binding = EncounterFragmentBinding.bind(view)

        (requireActivity() as MainActivity).hideKeyboard()

        with(binding) {
            toolbar.title = "Encounter : $code"
            setupView()
            setupSubscriptions()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.encounter_menu, menu)
        skipItem = menu.findItem(R.id.skipItem)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.skipItem) {
            viewModel.skipTurn()
        }
        return true
    }

    private fun EncounterFragmentBinding.setupSubscriptions() {

        viewModel.trackerLiveData.observe(viewLifecycleOwner) {
            val (tick, turnIndex, roundIndex, isPaused, isPlayPauseAllowed, isSkipTurnAllowed, participants) = it

            updateRound(roundIndex)
            updateTimer(tick)
            updatePlayPauseButton(isPlayPauseAllowed, isPaused)
            updateSkipTurnButton(isSkipTurnAllowed)
            updateCurrentTurnMarker(turnIndex)
            adapter.updateParticipants(participants)
        }
    }

    private fun updateSkipTurnButton(skipTurnAllowed: Boolean) {
        skipItem?.isVisible = skipTurnAllowed
    }

    private fun EncounterFragmentBinding.updateRound(roundIndex: Int) {
        roundNumberTextView.text = getString(R.string.round_index, roundIndex)
    }

    private fun EncounterFragmentBinding.updatePlayPauseButton(
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

    private fun updateCurrentTurnMarker(turnIndex: Int) {
        adapter.updateActiveParticipant(turnIndex)
    }

    private fun EncounterFragmentBinding.updateTimer(timerTick: Int) {
        val timer = timerFormatter.format(timerTick * 1000)
        val color = if (timerTick < 5) android.R.color.holo_red_light else R.color.primary_text
        roundTimerView.setTextColor(ContextCompat.getColor(requireContext(), color))
        roundTimerView.text = timer
    }

    private fun EncounterFragmentBinding.setupView() {
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
}