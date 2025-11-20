package com.eventmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eventmanagement.databinding.ItemEventBinding
import com.eventmanagement.models.Event
import com.eventmanagement.models.Registration

class RegistrationsAdapter(
    private val items: List<Pair<Event, Registration>>,
    private val onUnregisterClick: (String) -> Unit
) : RecyclerView.Adapter<RegistrationsAdapter.RegistrationViewHolder>() {

    inner class RegistrationViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, registration: Registration) {
            binding.tvTitle.text = event.title
            binding.tvDate.text = "Date: ${event.date}"
            binding.tvVenue.text = "Venue: ${event.venue}"
            binding.tvDescription.text = event.description
            binding.tvCapacity.text = "Status: ${registration.status.uppercase()}"
            binding.tvRewardPoints.text = if (registration.attended)
                "✓ Attended" else "⏳ Pending"

            binding.btnRegister.text = "Unregister"
            binding.btnRegister.setOnClickListener {
                onUnregisterClick(event.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistrationViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RegistrationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegistrationViewHolder, position: Int) {
        val (event, registration) = items[position]
        holder.bind(event, registration)
    }

    override fun getItemCount() = items.size
}