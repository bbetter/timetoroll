package com.owlsoft.turntoroll.androidApp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.owlsoft.turntoroll.androidApp.databinding.MenuFragmentBinding

class MenuFragment : Fragment(R.layout.menu_fragment) {

    lateinit var binding: MenuFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MenuFragmentBinding.bind(view)

        with(binding){

        }
    }
}