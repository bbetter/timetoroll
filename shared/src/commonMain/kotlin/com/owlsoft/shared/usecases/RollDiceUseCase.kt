package com.owlsoft.shared.usecases

import kotlin.random.Random

class RollDiceUseCase {
    companion object {
        const val LOW_MARGIN = 1
        const val HIGH_MARGIN = 20
    }

    fun execute(): Int = Random.nextInt(LOW_MARGIN, HIGH_MARGIN)
}