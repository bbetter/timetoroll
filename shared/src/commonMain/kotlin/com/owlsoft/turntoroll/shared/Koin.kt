package com.owlsoft.turntoroll.shared

import com.owlsoft.turntoroll.shared.data.EncounterManager
import com.owlsoft.turntoroll.shared.data.EncounterManagerImpl
import com.owlsoft.turntoroll.shared.domain.GetEncounterParticipantsUseCase
import com.owlsoft.turntoroll.shared.domain.ObserveActiveParticipantUseCase
import com.owlsoft.turntoroll.shared.domain.StartTimerUseCase
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin() = startKoin {
    modules(
        platformModule,
        coreModule
    )
}

private val coreModule = module {
    single<EncounterManager> { EncounterManagerImpl() }

    single { ObserveActiveParticipantUseCase(get()) }
    single { GetEncounterParticipantsUseCase(get()) }
    single { StartTimerUseCase(get()) }

}

expect val platformModule: Module