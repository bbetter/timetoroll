package com.owlsoft.turnit

import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.owlsoft.turnit.data.EncounterParticipant
import com.owlsoft.turntoroll.androidApp.ui.TurnitTheme
import com.owlsoft.turntoroll.androidApp.EncounterParticipantCard


val participants = listOf(
    EncounterParticipant("Archie", 23, 23, 1),
    EncounterParticipant("Wolf", 23, 23, 2),
    EncounterParticipant("Bugbear", 23, 23, 3),
    EncounterParticipant("Cookiegirl", 23, 23, 4),
    EncounterParticipant("My Low", 23, 23, 5),
    EncounterParticipant("Hende Hoch", 23, 23, 6),
)

@Composable
fun EncounterParticipantsScreen() {

    LazyColumnFor(participants) {
        EncounterParticipantCard(it)
    }
}

@Preview
@Composable
fun EncounterParticipantsScreenPreview() {

    TurnitTheme() {
        Surface(color = MaterialTheme.colors.background) {
            EncounterParticipantsScreen()
        }
    }
}