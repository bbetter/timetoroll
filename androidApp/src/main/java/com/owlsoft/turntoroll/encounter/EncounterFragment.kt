package com.owlsoft.turntoroll.encounter

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.model.RequestResult
import com.owlsoft.turntoroll.R
import com.owlsoft.turntoroll.databinding.CreateEncounterFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EncounterFragment : Fragment(R.layout.encounter_fragment) {

    lateinit var binding: CreateEncounterFragmentBinding

    private val adapter = EncounterParticipantsEditAdapter(
        onParticipantDelete = this::onParticipantDelete
    )

    private val viewModel: EncounterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CreateEncounterFragmentBinding.bind(view)

        with(binding) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            setupView()
            setupSubscription()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.create_encounter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.encounterDetailsActionButton) {
            lifecycleScope.launchWhenResumed {

                when (val result = viewModel.saveEncounter()) {
                    is RequestResult.Success -> {
                        findNavController().goToEncounter(result.code)
                    }
                    is RequestResult.Error -> {
                        showCreateEncounterError(result)
                    }
                }
            }
        }
        return true
    }

    private fun showCreateEncounterError(result: RequestResult.Error) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(result.message)
            .setPositiveButton("Cancel") { d, _ ->
                d.dismiss()
            }
            .create()
            .show()
    }

    private fun CreateEncounterFragmentBinding.setupView() {

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        participantsList.adapter = adapter

        participantsList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val initiative = initiativeEditText.text.toString()
            val dexterity = dexterityEditText.text.toString()

            viewModel.addParticipant(name, initiative, dexterity)

            nameEditText.text.clear()
            initiativeEditText.text.clear()
            dexterityEditText.text.clear()
        }
    }

    private fun onParticipantDelete(participant: Participant) {
        viewModel.removeParticipant(participant)
    }

    private fun setupSubscription() {
        viewModel.participantsLiveData.observe(viewLifecycleOwner) {
            adapter.updateParticipants(it)
        }
    }

    private fun NavController.goToEncounter(encounterCode: String) {
        navigate(
            R.id.action_encounterDetailsFragment_to_encounterFragment,
            bundleOf("code" to encounterCode)
        )
    }
}