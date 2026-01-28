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
) : RecyclerView.Adapter<RadioAdapter.VH>() {

    private var selectedStation: RadioStation? = null

    fun setSelectedStation(station: RadioStation) {
        selectedStation = station
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_radio, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val station = stations[position]

        holder.title.text = station.name
        holder.icon.setImageResource(station.iconRadio)

        holder.itemView.isSelected = station == selectedStation

        holder.itemView.setOnClickListener {
            onClick(station)
        }
    }

    override fun getItemCount(): Int = stations.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val icon: ImageView = view.findViewById(R.id.icRadio)
        val container: View = view.findViewById(R.id.container)
    }
}

