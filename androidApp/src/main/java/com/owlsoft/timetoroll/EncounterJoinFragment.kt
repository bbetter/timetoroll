package com.owlsoft.timetoroll

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.owlsoft.shared.viewmodel.EncounterJoinViewModel
import com.owlsoft.timetoroll.databinding.EncounterJoinFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EncounterJoinFragment : Fragment(R.layout.encounter_join_fragment) {

    private lateinit var binding: EncounterJoinFragmentBinding

    private val code by lazy { arguments?.getString("code") }

    private val adapter = EncounterParticipantsEditAdapter(
        mutableListOf(),
        onParticipantDelete = { viewModel.removeParticipant(it) }
    )
    private val viewModel: EncounterJoinViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.join_encounter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.encounterDetailsActionButton) {
            val encounterCode = binding.encounterCodeEditText.text.toString()

            viewModel.join(
                encounterCode,
                onSuccess = { findNavController().goToEncounter(encounterCode) },
                onError = { errorMessage -> showError(errorMessage) }
            )
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding = EncounterJoinFragmentBinding.bind(view)

        with(binding) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            setupView()
            setupSubscription()
        }
    }

    private fun showError(errorMessage: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error_title))
            .setMessage(errorMessage)
            .setPositiveButton(getString(R.string.error_confirmation_action_title)) { d, _ -> d.dismiss() }
            .create()
            .show()
    }

    private fun EncounterJoinFragmentBinding.setupView() {
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        participantsList.adapter = adapter
        participantsList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        encounterCodeEditText.setText(code ?: "")

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val initiative = initiativeEditText.text.toString().toIntOrNull() ?: 0
            val dexterity = dexterityEditText.text.toString().toIntOrNull() ?: 0

            viewModel.addParticipant(name, initiative, dexterity)

            nameEditText.text.clear()
            initiativeEditText.text.clear()
            dexterityEditText.text.clear()
        }
    }

    private fun setupSubscription() {
        viewModel.data.watch {
            adapter.updateParticipants(it)
        }
    }

    private fun NavController.goToEncounter(encounterCode: String) {
        navigate(
            R.id.action_encounterJoinFragment_to_encounterFragment,
            bundleOf("code" to encounterCode)
        )
    }
}