package com.owlsoft.turntoroll.encounter

import android.os.Bundle
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class EncounterFragment : Fragment(R.layout.encounter_fragment) {

    private lateinit var binding: EncounterFragmentBinding

    private val adapter = EncounterParticipantsAdapter()

    private val viewModel: EncounterViewModel by viewModel()
    private val timerFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val code = arguments?.getString("code") ?: ""

        Log.d("DEBUG", "CODE $code")
        viewModel.observeRoundInfo(code)
        viewModel.requestParticipants(code)

        binding = EncounterFragmentBinding.bind(view)

        (requireActivity() as MainActivity).hideKeyboard()
        with(binding) {
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
            viewModel.skip()
        }
        return true
    }

    private fun EncounterFragmentBinding.setupSubscriptions() {
        viewModel.participantsLiveData.observe(viewLifecycleOwner) {
            adapter.updateParticipants(it)
        }


        viewModel.roundLiveData.observe(viewLifecycleOwner) {
            val (countDown, turnIndex, roundIndex) = it

            showTimer(countDown)
            showActiveParticipant(turnIndex)
            showRound(roundIndex)
        }
    }

    private fun EncounterFragmentBinding.showRound(roundIndex: Int) {
        roundNumberTextView.text = getString(R.string.round_index, roundIndex)
    }

    private fun showActiveParticipant(turnIndex: Int) {
        adapter.updateActiveParticipant(turnIndex)
    }

    private fun EncounterFragmentBinding.showTimer(countDown: Long) {
        val timer = timerFormatter.format(countDown * 1000)
        val color = if (countDown < 5) android.R.color.holo_red_light else R.color.primary_text
        roundTimerView.setTextColor(ContextCompat.getColor(requireContext(), color))
        roundTimerView.text = timer
    }

    private fun EncounterFragmentBinding.setupView() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            val navController = findNavController()
            navController.popBackStack()
        }

        participantsList.adapter = adapter
        participantsList.layoutManager = LinearLayoutManager(
            context,
            VERTICAL,
            false
        )
    }
}