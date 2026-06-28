package com.example.sweetsmarthome.led

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sweetsmarthome.R

class LedEffectsAdapter(
    private val originalEffects: List<LedEffect>,
    private val onEffectClick: (LedEffect) -> Unit
) : RecyclerView.Adapter<LedEffectsAdapter.EffectViewHolder>() {

    private var filteredEffects: List<LedEffect> = originalEffects.toList()
    private var selectedCommand: String? = null

    inner class EffectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val cardView: CardView = view as CardView

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedEffect = filteredEffects[position]
                    selectedCommand = clickedEffect.command
                    notifyDataSetChanged()
                    onEffectClick(clickedEffect)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EffectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_led, parent, false)
        return EffectViewHolder(view)
    }

    override fun onBindViewHolder(holder: EffectViewHolder, position: Int) {
        val effect = filteredEffects[position]
        holder.tvTitle.text = effect.name
        val context = holder.itemView.context

        if (effect.command == selectedCommand) {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.purple_700)
            )
        } else {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.gray_transparent)
            )
        }
    }

    override fun getItemCount(): Int = filteredEffects.size

    fun filter(query: String) {
        filteredEffects = if (query.isEmpty()) {
            originalEffects
        } else {
            originalEffects.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedCommand = null
        notifyDataSetChanged()
    }
}