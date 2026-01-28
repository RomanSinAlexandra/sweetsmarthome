package com.example.sweetsmarthome.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RadioViewModel : ViewModel() {

    private val stations = RadioStations.list

    private val _currentStation = MutableLiveData<RadioStation>()
    val currentStation: LiveData<RadioStation> = _currentStation

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _errorEvent = MutableLiveData<String?>()
    val errorEvent: LiveData<String?> = _errorEvent

    private var currentIndex = 0

    init {
        if (stations.isNotEmpty()) {
            currentIndex = 0
            _currentStation.value = stations[0]
            _isPlaying.value = false
        }
    }

    fun onStationClicked(station: RadioStation) {
        val current = _currentStation.value
        if (current == station) {
            _isPlaying.value = !(_isPlaying.value ?: false)
        } else {
            selectStation(station)
        }
    }

    fun selectRandomStation() {
        if (stations.isEmpty()) return

        val randomStation = stations.random()
        if (randomStation != _currentStation.value) {
            selectStation(randomStation)
        } else {
            selectRandomStation()
        }
    }

    fun selectStation(station: RadioStation) {
        currentIndex = stations.indexOf(station)
        _currentStation.value = station
        _isPlaying.value = true
    }

    fun next() {
        if (currentIndex < stations.lastIndex) {
            currentIndex++
            _currentStation.value = stations[currentIndex]
            _isPlaying.value = true
        } else {
            _errorEvent.value = "Это последняя радиостанция"
        }
    }

    fun prev() {
        if (currentIndex > 0) {
            currentIndex--
            _currentStation.value = stations[currentIndex]
            _isPlaying.value = true
        } else {
            _errorEvent.value = "Это первая радиостанция"
        }
    }

    fun togglePlayPause() {
        _isPlaying.value = !(_isPlaying.value ?: false)
    }

    fun clearError() {
        _errorEvent.value = null
    }
}
