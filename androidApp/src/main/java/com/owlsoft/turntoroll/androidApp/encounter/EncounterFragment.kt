package com.owlsoft.turntoroll.androidApp.encounter

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.owlsoft.turntoroll.androidApp.R
import com.owlsoft.turntoroll.androidApp.databinding.EncounterFragmentBinding

class EncounterFragment : Fragment(R.layout.encounter_fragment) {
    private lateinit var binding: EncounterFragmentBinding
    private val adapter = EncounterParticipantsAdapter()

    private val viewModel by viewModels<EncounterViewModel> { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = EncounterFragmentBinding.bind(view)

        with(binding) {
            participantsList.adapter = adapter
            participantsList.layoutManager = LinearLayoutManager(
                context,
                VERTICAL,
                false
            )
            viewModel.participantsLiveData.observe(viewLifecycleOwner) {
                adapter.updateParticipants(it)
            }

            viewModel.activeParticipantLiveData.observe(viewLifecycleOwner) {
                adapter.updateActiveParticipant(it)
            }

            viewModel.timerLiveData.observe(viewLifecycleOwner) {
                val color = if (it < 5) android.R.color.holo_red_light else android.R.color.black
                timerView.setTextColor(ContextCompat.getColor(requireContext(), color))
                timerView.text = it.toString()
            }
        }
    }
}