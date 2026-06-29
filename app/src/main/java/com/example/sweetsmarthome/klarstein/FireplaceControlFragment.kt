package com.example.sweetsmarthome.fireplace

import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sweetsmarthome.BluetoothSharedViewModel
import com.example.sweetsmarthome.R
import java.util.*

class FireplaceControlFragment : Fragment(R.layout.fragment_fireplace) {

    private lateinit var sharedViewModel: BluetoothSharedViewModel
    private var isDeviceOn = true
    private var currentEffectId = 20

    private var isFireActive = false
    private var isFireLightActive = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        val vmClass = Class.forName("com.example.sweetsmarthome.BluetoothSharedViewModel") as Class<BluetoothSharedViewModel>
        sharedViewModel = ViewModelProvider(requireActivity()).get(vmClass)

        val btnPower = view.findViewById<ImageView>(R.id.btnPower)
        val tvStartTime = view.findViewById<TextView>(R.id.tvStartTime)
        val tvEndTime = view.findViewById<TextView>(R.id.tvEndTime)
        val cbTimer = view.findViewById<CheckBox>(R.id.cbTimer)
        val btnFire = view.findViewById<Button>(R.id.btnFire)
        val btnFireLight = view.findViewById<Button>(R.id.btnFireLight)

        // Устанавливаем начальное состояние кнопок при запуске экрана
        setButtonActive(btnFire)
        setButtonInactive(btnFireLight)

        // Клик по времени открытия TimePicker'а
        tvStartTime.setOnClickListener { showTimePicker(tvStartTime) }
        tvEndTime.setOnClickListener { showTimePicker(tvEndTime) }

        // Кнопка Питания по центру сверху (PWR:1 / PWR:0)
        btnPower.setOnClickListener {
            isDeviceOn = !isDeviceOn
            if (isDeviceOn) {
                btnPower.setColorFilter(Color.GREEN)
                sendCommand("PWR:1")
            } else {
                btnPower.setColorFilter(Color.GRAY)
                sendCommand("PWR:0")
            }
        }

        if (isFireActive) setButtonActive(btnFire) else setButtonInactive(btnFire)
        if (isFireLightActive) setButtonActive(btnFireLight) else setButtonInactive(btnFireLight)

        btnFire.setOnClickListener {
            isFireActive = !isFireActive // Меняем состояние на противоположное

            if (isFireActive) {
                setButtonActive(btnFire)
                // Включаем динамический эффект
                currentEffectId = if (currentEffectId == 20) 21 else if (currentEffectId == 21) 25 else 20
                sendCommand("EFF:$currentEffectId")
            } else {
                setButtonInactive(btnFire)
                // Отправляем команду выключения эффекта (например, EFF:0, если на ESP настроено выключение)
                sendCommand("EFF:0")
            }
        }

        btnFireLight.setOnClickListener {
            isFireLightActive = !isFireLightActive // Меняем состояние на противоположное

            if (isFireLightActive) {
                setButtonActive(btnFireLight)
                // Включаем статическое горение
                sendCommand("CLR:255,100,0")
            } else {
                setButtonInactive(btnFireLight)
                // Выключаем статическое горение, отправляя по нулям
                sendCommand("CLR:0,0,0")
            }
        }

        // Логика чекбокса таймера
        cbTimer.setOnCheckedChangeListener { _, isChecked ->
            val timeStart = tvStartTime.text.toString()
            val timeEnd = tvEndTime.text.toString()
            sendCommand("TIMER:${if (isChecked) "1" else "0"}:$timeStart-$timeEnd")
        }
    }

    // ИСПРАВЛЕНО: Функции вынесены из onViewCreated наружу, в тело класса
    private fun setButtonActive(button: Button) {
        // Приводим к AppCompatButton, чтобы гарантированно работал метод изменения тинта на старых версиях Android
        (button as? AppCompatButton)?.supportBackgroundTintList = null
    }

    private fun setButtonInactive(button: Button) {
        (button as? AppCompatButton)?.supportBackgroundTintList = android.content.res.ColorStateList.valueOf(
            Color.parseColor("#8E24AA")
        )
    }

    private fun showTimePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hour, minute ->
            textView.text = java.lang.String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun sendCommand(cmd: String) {
        sharedViewModel.bluetoothController?.sendMessage("$cmd\n")
    }
}