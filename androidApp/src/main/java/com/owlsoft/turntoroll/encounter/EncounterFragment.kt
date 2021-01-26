package com.owlsoft.turntoroll.encounter

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.owlsoft.shared.model.Participant
import com.owlsoft.turntoroll.EncounterService
import com.owlsoft.turntoroll.LocalRemoteEncounterTracker
import com.owlsoft.turntoroll.MainActivity
import com.owlsoft.turntoroll.R
import com.owlsoft.turntoroll.databinding.EncounterFragmentBinding
import kotlinx.coroutines.channels.consumeEach
import org.koin.android.ext.android.inject
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

    private val localRemoteEncounterTracker by inject<LocalRemoteEncounterTracker>()

    private val encounterService by lazy { Intent(requireContext(), EncounterService::class.java) }

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


        lifecycleScope.launchWhenResumed {
            localRemoteEncounterTracker.data.consumeEach {
                binding.updateTimer(it.tick)
                binding.updatePlayPauseButton(it.isPlayPauseAllowed, it.isPaused)
                binding.updateRound(it.roundIndex)

                updateParticipantsList(it.participants, it.turnIndex)
                updateSkipTurnButton(it.isSkipTurnAllowed)
            }
        }
    }

    private fun updateParticipantsList(participants: List<Participant>, turnIndex: Int) {
        adapter.updateParticipants(participants)
        adapter.updateActiveParticipant(turnIndex)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = EncounterFragmentBinding.bind(view)

        (requireActivity() as MainActivity).hideKeyboard()

        with(binding) {
            toolbar.title = "Encounter : $code"
            setupView()
        }

        startEncounterService()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.encounter_menu, menu)
        skipItem = menu.findItem(R.id.skipItem)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.skipItem) {
            lifecycleScope.launchWhenCreated {
                localRemoteEncounterTracker.commands.send("skip")
            }

//            viewModel.skipTurn()
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
            lifecycleScope.launchWhenCreated {
                localRemoteEncounterTracker.commands.send("resume")
            }
//            val currentState = viewModel.trackerLiveData.value ?: return@setOnClickListener
//
//            if (currentState.isPaused) {
//                viewModel.resume()
//            } else {
//                viewModel.pause()
//            }
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