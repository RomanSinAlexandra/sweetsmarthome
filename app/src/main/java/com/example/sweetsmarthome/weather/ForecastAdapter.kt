package com.example.sweetsmarthome.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.sweetsmarthome.R
import java.text.SimpleDateFormat
import java.util.Locale

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    private var items = listOf<ForecastItem>()

    // Форматеры для преобразования даты "2026-06-03 12:00:00" в красивый формат "03 Июня"
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd MMMM", Locale("ru"))

    fun submitList(list: List<ForecastItem>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.temp.text = "${item.main.temp.toInt()}°C"

        // Преобразуем строку с датой в читаемый формат
        try {
            val date = inputFormat.parse(item.dt_txt)
            holder.time.text = date?.let { outputFormat.format(it) } ?: item.dt_txt.substring(0, 10)
        } catch (e: Exception) {
            holder.time.text = item.dt_txt.substring(0, 10) // Резервный вариант
        }

        // Выбираем правильную анимацию для этого дня
        val condition = item.weather.firstOrNull()?.main ?: "Clear"
        val animRes = when (condition.lowercase()) {
            "clouds" -> R.raw.anim_cloudy
            "rain", "drizzle" -> R.raw.anim_rain
            "thunderstorm" -> R.raw.anim_thunder
            "snow" -> R.raw.anim_snow
            else -> R.raw.anim_sunny
        }

        // Устанавливаем и запускаем анимацию
        holder.animation.setAnimation(animRes)
        holder.animation.playAnimation()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val temp: TextView = view.findViewById(R.id.tvTemp)
        val time: TextView = view.findViewById(R.id.tvTime)
        // Добавили ссылку на LottieAnimationView из XML
        val animation: LottieAnimationView = view.findViewById(R.id.itemWeatherAnim)
    }
}