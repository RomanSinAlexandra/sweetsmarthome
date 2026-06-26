package com.example.sweetsmarthome.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sweetsmarthome.R
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _forecastData = MutableLiveData<ForecastResponse>()
    val forecastData: LiveData<ForecastResponse> get() = _forecastData

    private val _weatherAnim = MutableLiveData<Int>()
    val weatherAnim: LiveData<Int> get() = _weatherAnim

    private val apiKey = "c3228281a09bcebca1b0d642d6efe889" // Вставьте ваш ключ

    fun startAutoUpdate(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = WeatherService.api.getForecast(lat, lon, apiKey)
                _forecastData.value = response

                // Получаем текущую погоду (первый элемент из списка)
                val currentCondition = response.list.firstOrNull()?.weather?.firstOrNull()?.main ?: "Clear"
                _weatherAnim.value = getLottieAnimationForCondition(currentCondition)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Фильтруем данные, чтобы получить прогноз только на дневное время (12:00:00) каждого дня
    fun getDailyForecast(list: List<ForecastItem>): List<ForecastItem> {
        return list.filter { it.dt_txt.contains("12:00:00") }
    }

    private fun getLottieAnimationForCondition(condition: String): Int {
        return when (condition.lowercase()) {
            "clouds" -> R.raw.anim_cloudy
            "rain", "drizzle" -> R.raw.anim_rain
            "thunderstorm" -> R.raw.anim_thunder
            "snow" -> R.raw.anim_snow // Добавлено условие для снега
            else -> R.raw.anim_sunny
        }
    }
}