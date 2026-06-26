package com.example.sweetsmarthome.led

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sweetsmarthome.BluetoothSharedViewModel
import com.example.sweetsmarthome.R

class LedFunctionsFragment : Fragment(R.layout.fragment_led_function) {

    private lateinit var sharedViewModel: BluetoothSharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Подключаем ViewModel для отправки Bluetooth-команд
        @Suppress("UNCHECKED_CAST")
        val vmClass = Class.forName("com.example.sweetsmarthome.BluetoothSharedViewModel") as Class<BluetoothSharedViewModel>
        sharedViewModel = ViewModelProvider(requireActivity()).get(vmClass)

        val rcFunction = view.findViewById<RecyclerView>(R.id.rcFunction)
        rcFunction.layoutManager = LinearLayoutManager(requireContext())

        // Инициализируем адаптер, передаем список из LedFunction и настраиваем отправку команды по клику
        val adapter = LedEffectsAdapter(LedFunction.effects) { selectedEffect ->
            val fullCommand = "${selectedEffect.command}\n"
            sharedViewModel.bluetoothController?.sendMessage(fullCommand)
        }

        rcFunction.adapter = adapter
    }
}