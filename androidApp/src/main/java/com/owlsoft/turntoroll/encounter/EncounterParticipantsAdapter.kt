package com.owlsoft.turntoroll.encounter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.owlsoft.shared.model.Character
import com.owlsoft.turntoroll.R
import com.owlsoft.turntoroll.databinding.EncounterParticipantItemBinding

class EncounterParticipantsAdapter(
    private var data: List<Character> = emptyList(),
    private var activeParticipantIndex: Int = 0
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

    fun updateParticipants(characters: List<Character>) {
        this.data = characters
        notifyDataSetChanged()
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

        fun bind(character: Character, index: Int) {
            with(binding) {
                setupActiveParticipant(index)
                setupParticipantInitiative(character)
                nameTextView.text = character.name
            }
        }

        private fun EncounterParticipantItemBinding.setupParticipantInitiative(
            character: Character
        ) {
            val initiative = root.context.resources.getString(
                R.string.initiative,
                character.initiative,
                character.dexterity
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