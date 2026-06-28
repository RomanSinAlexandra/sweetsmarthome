package com.example.sweetsmarthome.radio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sweetsmarthome.R

class RadioAdapter(
    // Здесь мы берем исходный список как базу для фильтрации
    private val originalStations: List<RadioStation>,
    private val onClick: (RadioStation) -> Unit
) : RecyclerView.Adapter<RadioAdapter.VH>() {

    private var selectedStation: RadioStation? = null
    private var isPlaying: Boolean = false

    // ЭТОТ СПИСОК МЫ БУДЕМ ОТОБРАЖАТЬ
    private var filteredList: List<RadioStation> = originalStations.toList()

    fun setStatus(station: RadioStation?, playing: Boolean) {
        selectedStation = station
        isPlaying = playing
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalStations
        } else {
            originalStations.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_radio, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        // !!! ВАЖНО: берем станцию из filteredList !!!
        val station = filteredList[position]
        val context = holder.itemView.context

        holder.title.text = station.name
        holder.icon.setImageResource(station.iconRadio)

        if (station == selectedStation) {
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_700))
            val indicatorColor = if (isPlaying) R.color.green else R.color.red
            holder.indicator.setCardBackgroundColor(ContextCompat.getColor(context, indicatorColor))
        } else {
            holder.container.setBackgroundResource(R.drawable.bg_radio_item)
            holder.indicator.setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray_transparent))
        }

        holder.itemView.setOnClickListener {
            onClick(station)
        }
    }

    // !!! ВАЖНО: возвращаем размер filteredList !!!
    override fun getItemCount(): Int = filteredList.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val icon: ImageView = view.findViewById(R.id.icRadio)
        val container: View = view.findViewById(R.id.container)
        val indicator: CardView = view.findViewById(R.id.indicatorPlaying)
    }
}