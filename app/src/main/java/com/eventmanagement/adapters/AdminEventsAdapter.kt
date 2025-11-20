package com.eventmanagement.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eventmanagement.R
import com.eventmanagement.models.Event

class AdminEventsAdapter(
    private val events: List<Event>,
    private val onEditClick: (Event) -> Unit
) : RecyclerView.Adapter<AdminEventsAdapter.AdminEventViewHolder>() {

    inner class AdminEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvAdminTitle)
        private val tvDetails: TextView = itemView.findViewById(R.id.tvAdminDetails)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvAdminStatus)
        private val btnEdit: Button = itemView.findViewById(R.id.btnAdminEdit)

        fun bind(event: Event) {
            tvTitle.text = event.title
            tvDetails.text = "${event.date} | ${event.venue}"

            val percentage = if (event.capacity > 0) {
                (event.registeredCount * 100) / event.capacity
            } else 0

            tvStatus.text = "${event.registeredCount}/${event.capacity} registered ($percentage%)"

            btnEdit.setOnClickListener {
                onEditClick(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminEventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_event, parent, false)
        return AdminEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminEventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size
}