package com.owlsoft.turntoroll

import com.owlsoft.shared.di.AppLogger
import com.owlsoft.shared.viewmodel.EncounterJoinViewModel
import com.owlsoft.shared.viewmodel.EncounterSessionViewModel
import com.owlsoft.shared.viewmodel.EncounterViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
val appModule = module {

    viewModel { EncounterViewModel() }
    viewModel { EncounterJoinViewModel() }

    viewModel { params ->
        val (_, code) = params.component1<Pair<String, String>>()
        EncounterSessionViewModel(code)
    }
}