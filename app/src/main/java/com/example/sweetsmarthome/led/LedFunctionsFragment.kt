package com.example.sweetsmarthome.led

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sweetsmarthome.BluetoothSharedViewModel
import com.example.sweetsmarthome.R
import android.Manifest

class LedFunctionsFragment : Fragment(R.layout.fragment_led_function) {

    private lateinit var sharedViewModel: BluetoothSharedViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MyLog", "Разрешение на микрофон получено!")
        } else {
            Log.d("MyLog", "ВНИМАНИЕ: Разрешение на микрофон отклонено. Звуковые эффекты не будут работать.")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            // Если нет — запрашиваем у пользователя
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        @Suppress("UNCHECKED_CAST")
        val vmClass = Class.forName("com.example.sweetsmarthome.BluetoothSharedViewModel") as Class<BluetoothSharedViewModel>
        sharedViewModel = ViewModelProvider(requireActivity()).get(vmClass)

        val rcFunction = view.findViewById<RecyclerView>(R.id.rcFunction)
        val etSearchEffects = view.findViewById<EditText>(R.id.etSearchEffects)

        rcFunction.layoutManager = LinearLayoutManager(requireContext())

        val adapter = LedEffectsAdapter(LedFunction.effects) { selectedEffect ->
            // Если команда вида "EFF:26", достаем "26"
            val effectId = selectedEffect.command.substringAfter(":").toIntOrNull() ?: 0

            if (effectId in 26..32) {
                Log.d("MyLog", "Включаем аудиостриминг для эффекта: $effectId")
                sharedViewModel.startAudioStreaming()
            } else {
                Log.d("MyLog", "Выключаем аудиостриминг. Обычный режим: $effectId")
                sharedViewModel.stopAudioStreaming()
            }

            val fullCommand = "${selectedEffect.command}\n"
            Log.d("MyLog", "Sending command: $fullCommand")
            sharedViewModel.bluetoothController?.sendMessage(fullCommand)
        }
        rcFunction.adapter = adapter

        // --- ЛОГИКА ПОИСКА ---
        etSearchEffects?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s.toString())
            }
        })

        // --- ЛОВИМ СИГНАЛ СБРОСА ОТ ПЕРВОГО ФРАГМЕНТА ---
        sharedViewModel.clearEffectSelectionEvent.observe(viewLifecycleOwner) { shouldClear ->
            // Проверяем, что shouldClear именно true (игнорируем false)
            if (shouldClear == true) {
                adapter.clearSelection()

                // ВАЖНО: используем .postValue(false) или небольшую задержку,
                // чтобы не вызвать конфликт с текущим циклом обновления UI
                view.post {
                    sharedViewModel.clearEffectSelectionEvent.value = false
                }
            }
        }
    }
}