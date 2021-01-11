package com.owlsoft.turntoroll.encounter_join

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.owlsoft.turntoroll.R
import com.owlsoft.turntoroll.databinding.EncounterJoinFragmentBinding
import com.owlsoft.turntoroll.encounter.EncounterDetailsViewModel
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
                findNavController().popBackStack()
            }

            encounterJoinButton.setOnClickListener {
                lifecycleScope.launchWhenResumed {

                    viewModel.joinEncounter(
                        encounterCodeEditText.text.toString(),
                        nameEditText.text.toString(),
                        initiativeEditText.text.toString(),
                        dexterityEditText.text.toString(),
                    )

                    navigationController.navigate(
                        R.id.action_encounterJoinFragment_to_encounterFragment,
                        bundleOf(
                            "code" to encounterCodeEditText.text.toString()
                        )
                    )
                }
            }
        }
    }
}