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

        private val yellowColor =
            ContextCompat.getColor(binding.root.context, android.R.color.holo_orange_light)
        private val whiteColor = ContextCompat.getColor(binding.root.context, android.R.color.white)

        fun bind(participant: Participant, index: Int) {
            with(binding) {
                setupActiveParticipant(index)
                setupParticipantInitiative(participant)
                nameTextView.text = participant.name
            }
        }

        private fun EncounterParticipantItemBinding.setupParticipantInitiative(
            participant: Participant
        ) {
            val initiative = root.context.resources.getString(
                R.string.initiative,
                participant.initiative,
                participant.dexterity
            )

            initiativeView.text = initiative
        }

        private fun EncounterParticipantItemBinding.setupActiveParticipant(
            participantIndex: Int
        ) {
            val color = if (participantIndex == activeParticipantIndex) {
                yellowColor
            } else {
                whiteColor
            }
            root.setCardBackgroundColor(color)
        }
    }
}