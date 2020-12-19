package com.owlsoft.turntoroll.androidApp.encounter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.owlsoft.turntoroll.androidApp.R
import com.owlsoft.turntoroll.androidApp.databinding.EncounterParticipantItemBinding
import com.owlsoft.turntoroll.shared.data.EncounterParticipant

class EncounterParticipantsAdapter(
    private var data: List<EncounterParticipant> = emptyList(),
    private var activeParticipant: Int = 0
) : RecyclerView.Adapter<EncounterParticipantsAdapter.EncounterParticipantViewHolder>() {

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

    fun updateParticipants(participants: List<EncounterParticipant>) {
        this.data = participants
        notifyDataSetChanged()
    }

    fun updateActiveParticipant(index: Int) {
        activeParticipant = index
        notifyDataSetChanged()
    }

    inner class EncounterParticipantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = EncounterParticipantItemBinding.bind(view)

        private val yellowColor =
            ContextCompat.getColor(binding.root.context, android.R.color.holo_orange_light)
        private val whiteColor = ContextCompat.getColor(binding.root.context, android.R.color.white)

        fun bind(encounterParticipant: EncounterParticipant, index: Int) {
            with(binding) {
                val color = if (index == activeParticipant) {
                    yellowColor
                } else {
                    whiteColor
                }
                root.setCardBackgroundColor(color)
                nameTextView.text = encounterParticipant.name
                initiativeView.text =
                    "${encounterParticipant.initiative}.${encounterParticipant.dexterity}"
            }
        }
    }
}