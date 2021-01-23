package com.owlsoft.turntoroll

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.owlsoft.turntoroll.databinding.MenuFragmentBinding

class MenuFragment : Fragment(R.layout.menu_fragment) {

    lateinit var binding: MenuFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = MenuFragmentBinding.bind(view)

        val navController = findNavController()

        with(binding) {

            createEncounterButton.setOnClickListener {
                navController.navigate(R.id.action_menuFragment_to_encounterDetailsFragment)
            }

            joinEncounterButton.setOnClickListener {
                navController.navigate(R.id.action_menuFragment_to_encounterJoinFragment)
            }
        }
    }
}