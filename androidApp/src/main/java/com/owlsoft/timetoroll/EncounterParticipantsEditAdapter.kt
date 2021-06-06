package com.owlsoft.timetoroll

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.owlsoft.shared.model.Participant
import com.owlsoft.timetoroll.databinding.EncounterParticipantEditItemBinding

class EncounterParticipantsEditAdapter(
    private var data: MutableList<Participant> = mutableListOf(),
    private val onParticipantDelete: (Participant) -> Unit = {}
) : RecyclerView.Adapter<EncounterParticipantsEditAdapter.EncounterParticipantViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EncounterParticipantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.encounter_participant_edit_item, parent, false)
        return EncounterParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: EncounterParticipantViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    fun updateParticipants(participants: List<Participant>) {
        this.data.clear()
        this.data.addAll(participants)
        notifyDataSetChanged()
    }

    inner class EncounterParticipantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = EncounterParticipantEditItemBinding.bind(view)

        init {
            binding.deleteItemButton.setOnClickListener {
                val participant = data[adapterPosition]
                onParticipantDelete(participant)
            }
        }

        fun bind(participant: Participant) {
            with(binding) {
                nameTextView.text = participant.name
                initiativeView.text = participant.initiative.toString()
                dexView.text = when {
                    participant.dexterity > 0 -> "+${participant.dexterity}"
                    participant.dexterity < 0 -> "${participant.dexterity}"
                    else -> "0"
                }
            }
        }
    }
}