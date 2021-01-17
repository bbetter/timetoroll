package com.owlsoft.shared.usecases

import com.owlsoft.shared.remote.TrackerAPI

class SkipTurnUseCase(
    private val api: TrackerAPI
) {
    suspend fun execute() {
        api.skipTurn()
    }
}