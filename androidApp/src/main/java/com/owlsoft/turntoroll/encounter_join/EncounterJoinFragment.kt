package com.owlsoft.turntoroll.encounter_join

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
import com.owlsoft.shared.usecases.JoinEncounterResult
import com.owlsoft.turntoroll.R
import com.owlsoft.turntoroll.databinding.EncounterJoinFragmentBinding
import com.owlsoft.turntoroll.encounter.EncounterParticipantsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class EncounterJoinFragment : Fragment(R.layout.encounter_join_fragment) {
    private lateinit var binding: EncounterJoinFragmentBinding

    private val adapter = EncounterParticipantsAdapter()
    private val viewModel: EncounterJoinViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.encounter_join_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.encounterDetailsActionButton) {
            lifecycleScope.launchWhenResumed {
                val encounterCode = binding.encounterCodeEditText.text.toString()

                val result = viewModel.joinEncounter(encounterCode)

                when (result) {
                    is JoinEncounterResult.Success -> {
                        findNavController().goToEncounter(encounterCode)
                    }
                    is JoinEncounterResult.Error -> {
                        showJoinEncounterError(result)
                    }
                }
            }
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

    private fun showJoinEncounterError(result: JoinEncounterResult.Error) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(result.message)
            .setPositiveButton("Cancel") { d, _ -> d.dismiss() }
            .create()
            .show()
    }

    private fun EncounterJoinFragmentBinding.setupView() {
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val initiative = initiativeEditText.text.toString()
            val dexterity = dexterityEditText.text.toString()

            viewModel.addParticipant(name, initiative, dexterity)
        }
    }

    private fun setupSubscription() {
        viewModel.participantsLiveData.observe(viewLifecycleOwner) {
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