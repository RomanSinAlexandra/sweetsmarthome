package com.example.sweetsmarthome.fireplace

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sweetsmarthome.BluetoothSharedViewModel
import com.example.sweetsmarthome.R
import java.util.*

class FireplaceControlFragment : Fragment(R.layout.fragment_fireplace) {

    private lateinit var sharedViewModel: BluetoothSharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        val vmClass = Class.forName("com.example.sweetsmarthome.BluetoothSharedViewModel") as Class<BluetoothSharedViewModel>
        sharedViewModel = ViewModelProvider(requireActivity()).get(vmClass)

        val btnPower = view.findViewById<ImageView>(R.id.btnPower)
        val tvStartTime = view.findViewById<TextView>(R.id.tvStartTime)
        val tvEndTime = view.findViewById<TextView>(R.id.tvEndTime)
        val cbTimer = view.findViewById<CheckBox>(R.id.cbTimer)
        val btnFireLight = view.findViewById<Button>(R.id.btnFireLight)

        // Обработка времени
        tvStartTime.setOnClickListener { showTimePicker(tvStartTime) }
        tvEndTime.setOnClickListener { showTimePicker(tvEndTime) }

        btnPower.setOnClickListener { sendCommand("FIRE_PWR") }

        btnFireLight.setOnClickListener {
            sendCommand("FIRE_MODE_LIGHT")
        }

        cbTimer.setOnCheckedChangeListener { _, isChecked ->
            val timeStart = tvStartTime.text.toString()
            val timeEnd = tvEndTime.text.toString()
            sendCommand("TIMER:${if (isChecked) "1" else "0"}:$timeStart-$timeEnd")
        }
    }

    private fun showTimePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hour, minute ->
            // Исправлено: использование String.format как статического метода Java
            textView.text = java.lang.String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun sendCommand(cmd: String) {
        sharedViewModel.bluetoothController?.sendMessage("$cmd\n")
    }
}