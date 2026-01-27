package com.example.sweetsmarthome.radio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sweetsmarthome.R

class RadioAdapter(
    private val stations: List<RadioStation>,
    private val onClick: (RadioStation) -> Unit
) : RecyclerView.Adapter<RadioAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val icon: ImageView = view.findViewById(R.id.icRadio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_radio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val station = stations[position]
        holder.title.text = station.name
        holder.icon.setImageResource(station.iconRadio)
        holder.itemView.setOnClickListener { onClick(station)
        }
    }

    override fun getItemCount() = stations.size
}
