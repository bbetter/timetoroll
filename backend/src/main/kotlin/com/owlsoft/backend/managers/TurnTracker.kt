package com.owlsoft.backend.managers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.Serializable


@Serializable
data class TrackerData(
    val timerTick: Int = 0,
    val turnIndex: Int = 0,
    val roundIndex: Int = 0,
    val isPaused: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
class TurnTracker(
    private val timePerTurn: Int,
    private var turnsPerRound: Int,
    private var isPaused: Boolean
) {

    companion object {
        const val TIMER_TICK = 1000L
    }

    private var _turnIndex = 0
    private var _roundIndex = 1
    private var _tick = timePerTurn

    val ticker = ticker(TIMER_TICK, 0)

    fun track(): Flow<TrackerData> {

        return ticker
            .consumeAsFlow()
            .transform {
                val newTimerValue = if (isPaused) _tick else _tick--

                emit(
                    TrackerData(
                        newTimerValue,
                        _turnIndex,
                        _roundIndex,
                        isPaused
                    )
                )
                if (_tick == 0) {
                    nextTurn()
                }
            }
    }

    fun nextTurn() {
        _tick = timePerTurn

        if (_turnIndex == turnsPerRound - 1) {
            _turnIndex = 0
            _roundIndex++
        } else {
            _turnIndex++
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    fun currentTurn() = _turnIndex

    fun updateTurnCount(turnsPerRound: Int) {
        this.turnsPerRound = turnsPerRound
    }

    fun cancel() {
        ticker.cancel()
    }
}