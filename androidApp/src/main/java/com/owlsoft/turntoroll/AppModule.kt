package com.owlsoft.turntoroll

import com.owlsoft.turntoroll.encounter.EncounterViewModel
import com.owlsoft.turntoroll.encounter.EncounterSessionViewModel
import com.owlsoft.turntoroll.encounter_join.EncounterJoinViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@OptIn(InternalCoroutinesApi::class)
val appModule = module {
//    viewModel { params -> EncounterViewModel( get { params } ) }

    single { LocalRemoteEncounterTracker() }

    viewModel { EncounterSessionViewModel() }
    viewModel { EncounterViewModel(get()) }
    viewModel { EncounterJoinViewModel(get()) }


}