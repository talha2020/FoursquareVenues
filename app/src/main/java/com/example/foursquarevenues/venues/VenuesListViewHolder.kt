package com.example.foursquarevenues.venues

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.foursquarevenues.GenericAdapter
import com.example.foursquarevenues.R
import com.example.foursquarevenues.data.Venue

class VenuesListViewHolder(
    itemView: View,
    val onItemClick: (Venue) -> Unit
) : RecyclerView.ViewHolder(itemView), GenericAdapter.Binder<Venue>  {

    private val listItem = itemView.findViewById<ConstraintLayout>(R.id.venueListItem)
    private val name = itemView.findViewById<TextView>(R.id.name)
    private val address = itemView.findViewById<TextView>(R.id.address)
    private val distance = itemView.findViewById<TextView>(R.id.distance)

    override fun bind(data: Venue) {
        name.text = data.name
        address.text = data.location.address
        distance.text = getFormattedDistance(data.location.distance)
        listItem.setOnClickListener { onItemClick(data) }
    }

    private fun getFormattedDistance(distance: Double): String {
        return if (distance > 1000){
            itemView.context.getString(R.string.km_away, distance.div(1000))
        } else {
            itemView.context.getString(R.string.m_away, distance)
        }
    }

}