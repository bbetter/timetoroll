package com.owlsoft.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class TickData(
    val tick: Int,
    val turnIndex: Int,
    val roundIndex: Int,
    val isPaused: Boolean,
    val isPlayPauseAllowed: Boolean,
    val isSkipTurnAllowed: Boolean,
    val participants: List<Participant>,
) {

    val isInDangerZone: Boolean
        get() = tick < 5
    val decoratedTick: String
        get() {
            val second: Int = tick % 60
            var minute: Int = tick / 60
            if (minute >= 60) {
                val hour = minute / 60
                minute %= 60
                return hour.toString() + ":" + (if (minute < 10) "0$minute" else minute) + ":" + if (second < 10) "0$second" else second
            }
            return minute.toString() + ":" + if (second < 10) "0$second" else second
        }
}