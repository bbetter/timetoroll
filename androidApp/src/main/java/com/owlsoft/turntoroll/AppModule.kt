package com.owlsoft.turntoroll

import com.owlsoft.turntoroll.encounter.EncounterDetailsViewModel
import com.owlsoft.turntoroll.encounter.EncounterViewModel
import com.owlsoft.turntoroll.encounter_join.EncounterJoinViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@OptIn(InternalCoroutinesApi::class)
val appModule = module {
    viewModel { params -> EncounterViewModel(get { params }, get()) }
    viewModel { EncounterDetailsViewModel(get()) }
    viewModel { EncounterJoinViewModel(get()) }
}