package com.example.sweetsmarthome.weather

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sweetsmarthome.R
import com.example.sweetsmarthome.databinding.FragmentWeatherBinding
import com.google.android.material.snackbar.Snackbar

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var locationHelper: LocationHelper
    private val forecastAdapter = ForecastAdapter()

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                requestLocation()
            } else {
                Snackbar.make(
                    requireView(),
                    "Разреши геолокацию для показа погоды",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWeatherBinding.bind(view)

        // Теперь список ВЕРТИКАЛЬНЫЙ (для прогноза по дням)
        binding.rvForecast.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvForecast.adapter = forecastAdapter

        locationHelper = LocationHelper(requireContext())
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        observeWeather()
    }

    private fun requestLocation() {
        locationHelper.getLocation(
            onResult = { lat, lon ->
                viewModel.startAutoUpdate(lat, lon)
            },
            onError = {
                Snackbar.make(requireView(), "Не удалось получить геолокацию", Snackbar.LENGTH_SHORT).show()
            }
        )
    }

    private fun observeWeather() {
        viewModel.weatherAnim.observe(viewLifecycleOwner) { animRes ->
            animateWeatherChange(animRes)
        }

        viewModel.forecastData.observe(viewLifecycleOwner) { response ->
            // Обновляем текущую погоду на главном экране
            val currentItem = response.list.firstOrNull()
            if (currentItem != null) {
                binding.tvCity.text = response.city.name
                binding.tvTemp.text = "${currentItem.main.temp.toInt()}°C"
                binding.tvDesc.text = currentItem.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
            }

            // Получаем отфильтрованный список (по 1 записи на день) и передаем в адаптер
            val dailyForecast = viewModel.getDailyForecast(response.list)
            forecastAdapter.submitList(dailyForecast)
        }
    }

    private fun animateWeatherChange(newAnim: Int) {
        binding.weatherAnim.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.weatherAnim.setAnimation(newAnim)
                binding.weatherAnim.playAnimation()

                binding.weatherAnim.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}