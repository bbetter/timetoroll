package com.owlsoft.timetoroll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.owlsoft.shared.model.Participant
import com.owlsoft.timetoroll.databinding.EncounterParticipantItemBinding

class EncounterSessionParticipantsAdapter(
    private var data: List<Participant> = emptyList(),
    private var activeParticipantIndex: Int = -1
) : RecyclerView.Adapter<EncounterSessionParticipantsAdapter.EncounterParticipantViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EncounterParticipantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.encounter_participant_item, parent, false)
        return EncounterParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: EncounterParticipantViewHolder, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount() = data.size

    fun updateParticipants(participants: List<Participant>) {
        if (data != participants) {
            this.data = participants
            notifyDataSetChanged()
        } else {
            this.data = participants
        }
    }

    fun updateActiveParticipant(index: Int) {
        activeParticipantIndex = index
        notifyDataSetChanged()
    }

    inner class EncounterParticipantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = EncounterParticipantItemBinding.bind(view)

        fun bind(participant: Participant, index: Int) {
            with(binding) {
                nameTextView.text = participant.name
                setupActiveParticipant(index)
                setupParticipantInitiative(participant)
            }
        }

        private fun EncounterParticipantItemBinding.setupParticipantInitiative(
            participant: Participant
        ) {

            initiativeView.text = participant.initiative.toString()
            dexView.text = when {
                participant.dexterity > 0 -> "+${participant.dexterity}"
                participant.dexterity < 0 -> "${participant.dexterity}"
                else -> "0"
            }
        }

        private fun EncounterParticipantItemBinding.setupActiveParticipant(
            participantIndex: Int
        ) {
            activeIndicator.visibility = if (participantIndex == activeParticipantIndex) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}