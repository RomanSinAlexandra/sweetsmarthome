package com.example.sweetsmarthome.led

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sweetsmarthome.R

class LedEffectsAdapter(
    private val effects: List<LedEffect>,
    private val onEffectClick: (LedEffect) -> Unit
) : RecyclerView.Adapter<LedEffectsAdapter.EffectViewHolder>() {

    // Храним позицию текущего выбранного эффекта (-1 означает, что ничего не выбрано)
    private var selectedPosition = RecyclerView.NO_POSITION

    inner class EffectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)

        // Корневой элемент вашей разметки - CardView. Приводим view к этому типу,
        // чтобы напрямую менять его свойство CardBackgroundColor
        val cardView: CardView = view as CardView

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    // 1. Запоминаем старую позицию, чтобы "погасить" её подсветку
                    val previousPosition = selectedPosition

                    // 2. Обновляем позицию на ту, по которой кликнули
                    selectedPosition = position

                    // 3. Даем команду адаптеру перерисовать ТОЛЬКО две карточки (старую и новую),
                    // это работает намного быстрее и плавнее, чем notifyDataSetChanged()
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)

                    // 4. Отправляем команду контроллеру
                    onEffectClick(effects[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EffectViewHolder {
        // Убедитесь, что здесь указано правильное имя вашего XML-файла карточки
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_led, parent, false)
        return EffectViewHolder(view)
    }

    fun clearSelection() {
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged() // Это заставит перерисовать все элементы
    }

    override fun onBindViewHolder(holder: EffectViewHolder, position: Int) {
        val effect = effects[position]
        holder.tvTitle.text = effect.name
        val context = holder.itemView.context

        // Если position не совпадает с выбранным, всегда ставим серый цвет
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.purple_700)
            )
        } else {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.gray_transparent)
            )
        }
    }

    override fun getItemCount(): Int = effects.size
}