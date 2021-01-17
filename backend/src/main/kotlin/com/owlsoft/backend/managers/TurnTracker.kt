package com.owlsoft.backend.managers

import com.owlsoft.backend.data.TrackerData
import io.ktor.server.engine.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
class TurnTracker(
    turnsPerRound: Int,
    timePerTurn: Int,
) {

    companion object {
        const val TIMER_TICK = 1000L
    }

    private val turnsPerRoundChannel = ConflatedBroadcastChannel(turnsPerRound)
    private val timePerTurnChannel = ConflatedBroadcastChannel(timePerTurn)

    private var _turnIndex = 0
    private var _roundIndex = 1

    fun observeResults(): Flow<TrackerData> =
        combine(
            turnsPerRoundChannel.asFlow(),
            timePerTurnChannel.asFlow()
        ) { _, turnTime -> turnTime }
            .flatMapLatest { turnTime ->
                var timerTick = turnTime
                ticker(TIMER_TICK)
                    .consumeAsFlow()
                    .map {
                        if (timerTick == 0) {
                            timerTick = turnTime
                            nextTurn()
                        }
                        TrackerData(
                            timerTick--,
                            _turnIndex,
                            _roundIndex
                        )
                    }
            }

    suspend fun nextTurn() {

        timePerTurnChannel.send(timePerTurnChannel.value)

        if (_turnIndex == turnsPerRoundChannel.value - 1) {
            _turnIndex = 0
            _roundIndex++
        } else {
            _turnIndex++
        }
    }

    suspend fun updateConfig(turnsPerRound: Int, singleTurnSeconds: Int) {
        timePerTurnChannel.send(singleTurnSeconds)
        turnsPerRoundChannel.send(turnsPerRound)
    }
}