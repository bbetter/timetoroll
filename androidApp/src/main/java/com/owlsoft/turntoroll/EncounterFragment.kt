package com.owlsoft.turntoroll

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.owlsoft.shared.model.Participant
import com.owlsoft.shared.model.RequestResult
import com.owlsoft.shared.viewmodel.EncounterViewModel
import com.owlsoft.turntoroll.databinding.EncounterFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EncounterFragment : Fragment(R.layout.encounter_fragment) {

    lateinit var binding: EncounterFragmentBinding

    private val adapter = EncounterParticipantsEditAdapter(
        onParticipantDelete = this::onParticipantDelete
    )

    private val code by lazy { arguments?.getString("code") }

    private val viewModel: EncounterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        code?.let {
            viewModel.requestParticipants(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EncounterFragmentBinding.bind(view)

        with(binding) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            setupView()
            setupSubscription()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.encounter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_menu_item) {
            if (code != null) {
                viewModel.update(
                    code!!,
                    onSuccess = { findNavController().popBackStack() },
                    onError = { showError(it) }
                )
            } else {
                viewModel.create(
                    onSuccess = { findNavController().goToEncounter(it) },
                    onError = { showError(it) }
                )
            }
        }
        return true
    }

    private fun showError(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_title)
            .setMessage(message)
            .setPositiveButton(R.string.error_confirmation_action_title) { d, _ ->
                d.dismiss()
            }
            .create()
            .show()
    }

    private fun EncounterFragmentBinding.setupView() {

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
            val initiative = initiativeEditText.text.toString().toIntOrNull() ?: 0
            val dexterity = dexterityEditText.text.toString().toIntOrNull() ?: 0

            viewModel.addParticipant(name, initiative, dexterity)

            nameEditText.text.clear()
            initiativeEditText.text.clear()
            dexterityEditText.text.clear()
        }
    }

    private fun onParticipantDelete(participant: Participant) {
        viewModel.removeParticiapant(participant)
    }

    private fun setupSubscription() {
        viewModel.data.watch {
            adapter.updateParticipants(it)
        }
    }

    private fun NavController.goToEncounter(encounterCode: String) {
        navigate(
            R.id.action_encounterFragment_to_encounterSessionFragment,
            bundleOf("code" to encounterCode)
        )
    }

}