package com.owlsoft.turntoroll.encounter_join

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.owlsoft.shared.usecases.JoinEncounterResult
import com.owlsoft.turntoroll.R
import com.owlsoft.turntoroll.databinding.EncounterJoinFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EncounterJoinFragment : Fragment(R.layout.encounter_join_fragment) {
    private lateinit var binding: EncounterJoinFragmentBinding

    private val viewModel: EncounterJoinViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding = EncounterJoinFragmentBinding.bind(view)

        val navigationController = findNavController()

        with(binding) {

            toolbar.setNavigationOnClickListener {
                navigationController.popBackStack()
            }

            encounterJoinButton.setOnClickListener {
                lifecycleScope.launchWhenResumed {
                    val encounterCode = encounterCodeEditText.text.toString()
                    val name = nameEditText.text.toString()
                    val initiative = initiativeEditText.text.toString()
                    val dexterity = dexterityEditText.text.toString()

                    val result = viewModel.joinEncounter(
                        encounterCode,
                        name,
                        initiative,
                        dexterity,
                    )

                    when (result) {
                        is JoinEncounterResult.Success -> {
                            navigationController.goToEncounter(encounterCode)
                        }
                        is JoinEncounterResult.Error -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Error")
                                .setMessage(result.message)
                                .setPositiveButton("Cancel") { d, _ -> d.dismiss() }
                                .create()
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun NavController.goToEncounter(encounterCode: String) {
        navigate(
            R.id.action_encounterJoinFragment_to_encounterFragment,
            bundleOf("code" to encounterCode)
        )
    }
}