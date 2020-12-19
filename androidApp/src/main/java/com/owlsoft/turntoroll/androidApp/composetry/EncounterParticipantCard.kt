//package com.owlsoft.turntoroll.androidApp
//
//import EncounterParticipant
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.BasicText
//import androidx.compose.material.Card
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountCircle
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.owlsoft.turntoroll.androidApp.ui.typography
//
//@Composable
//fun EncounterParticipantCard(ep: EncounterParticipant) {
//    Card(elevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Image(
//                imageVector = Icons.Filled.AccountCircle,
//                modifier = Modifier.size(48.dp)
//                    .padding(12.dp)
//            )
//            Column {
//                BasicText(ep.name, style = typography.h6)
//                BasicText(
//                    "HP: ${ep.currentHitPoints}/${ep.maxHitPoints}",
//                    style = typography.subtitle1
//                )
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//fun EncounterParticipantCardPreview() {
//    val participant = EncounterParticipant("Archie", 23, 23, 1)
//    EncounterParticipantCard(participant)
//}
