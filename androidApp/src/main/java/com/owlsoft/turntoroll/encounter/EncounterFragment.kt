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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class EncounterFragment : Fragment(R.layout.encounter_fragment) {

    private lateinit var binding: EncounterFragmentBinding

    private val adapter = EncounterParticipantsAdapter()

    private val timerFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val code by lazy { arguments?.getString("code") ?: "" }
    private val viewModel: EncounterViewModel by viewModel {
        parametersOf(
            "code" to arguments?.getString(
                "code"
            ) ?: ""
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.requestParticipants(code)

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
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.skipItem) {
            viewModel.skipTurn()
        }
        return true
    }

    private fun EncounterFragmentBinding.setupSubscriptions() {
        viewModel.participantsLiveData.observe(viewLifecycleOwner) {
            swipeRefreshLayout.isRefreshing = false
            adapter.updateParticipants(it)
        }

        viewModel.encounterData.observe(viewLifecycleOwner) {
            val (timerTick, turnIndex, roundIndex, isPaused: Boolean) = it

            updateRound(roundIndex)
            updateTimer(timerTick)
            updatePaused(isPaused)
            updateCurrentTurnMarker(turnIndex)
        }
    }

    private fun EncounterFragmentBinding.updateRound(roundIndex: Int) {
        roundNumberTextView.text = getString(R.string.round_index, roundIndex)
    }

    private fun EncounterFragmentBinding.updatePaused(isPaused: Boolean) {
        val resource =
            if (!isPaused) R.drawable.ic_baseline_pause_circle_filled_24 else R.drawable.ic_baseline_play_arrow_24
        playPauseButton.setImageResource(resource)
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

        swipeRefreshLayout.setOnRefreshListener {
            try {
                viewModel.requestParticipants(code)
            } catch (ex: Exception) {
                swipeRefreshLayout.isRefreshing = false
            }
        }

        playPauseButton.setOnClickListener {
            val currentState = viewModel.encounterData.value ?: return@setOnClickListener

            if (currentState.isPaused) {
                viewModel.resume()
            }
            else{
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