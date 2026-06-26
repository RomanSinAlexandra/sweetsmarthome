package com.example.sweetsmarthome.weather

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: MainData,
    val weather: List<WeatherDescription>,
    val dt_txt: String
)

data class MainData(
    val temp: Double,
    val feels_like: Double
)

data class WeatherDescription(
    val main: String,
    val description: String,
    val icon: String
)

data class City(
    val name: String
)