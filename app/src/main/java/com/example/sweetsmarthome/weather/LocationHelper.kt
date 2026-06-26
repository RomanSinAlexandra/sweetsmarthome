package com.example.sweetsmarthome.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationHelper(context: Context) {

    private val client = LocationServices.getFusedLocationProviderClient(context)
    private val appContext = context.applicationContext

    @SuppressLint("MissingPermission")
    fun getLocation(onResult: (Double, Double) -> Unit, onError: () -> Unit) {
        if (
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onError()
            return
        }

        // Пытаемся получить lastLocation
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onResult(location.latitude, location.longitude)
            } else {
                // fallback — принудительное получение
                client.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).addOnSuccessListener { loc ->
                    if (loc != null) {
                        onResult(loc.latitude, loc.longitude)
                    } else {
                        onError()
                    }
                }
            }
        }
    }
}
