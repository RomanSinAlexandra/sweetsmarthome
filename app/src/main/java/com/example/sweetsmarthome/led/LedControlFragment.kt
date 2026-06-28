package com.example.sweetsmarthome.led

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sweetsmarthome.BluetoothSharedViewModel
import com.example.sweetsmarthome.R
import com.google.android.material.card.MaterialCardView
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ActionMode
import me.tankery.lib.circularseekbar.CircularSeekBar

class LedControlFragment : Fragment(R.layout.fragment_led) {

    private lateinit var sharedViewModel: BluetoothSharedViewModel
    private var lastCommandTime = 0L
    private var isPowerOn = false
    private lateinit var prefs: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        val vmClass = Class.forName("com.example.sweetsmarthome.BluetoothSharedViewModel") as Class<BluetoothSharedViewModel>
        sharedViewModel = ViewModelProvider(requireActivity()).get(vmClass)

        prefs = requireActivity().getSharedPreferences("LedControlPrefs", Context.MODE_PRIVATE)

        val btnPower = view.findViewById<ImageView>(R.id.btnPower)
        val sbBrightness = view.findViewById<SeekBar>(R.id.sbBrightness)
        val colorPicker = view.findViewById<ColorPickerView>(R.id.colorPicker)
        val currentCenterColor = view.findViewById<MaterialCardView>(R.id.currentColorCenter)
        val circularSeekBar = view.findViewById<CircularSeekBar>(R.id.circularSeekBar)

        // --- ИНИЦИАЛИЗАЦИЯ СЕЛЕКТОРА ---
        colorPicker?.setActionMode(ActionMode.LAST)

        // --- ВОССТАНОВЛЕНИЕ СОСТОЯНИЯ ---
        isPowerOn = prefs.getBoolean("power", true)
        val savedBrightness = prefs.getInt("brightness", 150)
        val savedColor = prefs.getInt("color", Color.WHITE)

        btnPower?.alpha = if (isPowerOn) 1.0f else 0.4f
        sbBrightness?.progress = savedBrightness
        colorPicker?.setInitialColor(savedColor)
        currentCenterColor?.setCardBackgroundColor(savedColor)
        circularSeekBar?.progress = getHueFromColor(savedColor)
        circularSeekBar?.circleProgressColor = savedColor

        view.postDelayed({
            sendCommand(if (isPowerOn) "PWR:1" else "PWR:0")
            sendCommand("BRT:$savedBrightness")
            sendColorCommand(savedColor)
        }, 300)

        // --- ЛОГИКА ПИТАНИЯ ---
        btnPower?.setOnClickListener {
            isPowerOn = !isPowerOn
            prefs.edit().putBoolean("power", isPowerOn).apply()
            sendCommand(if (isPowerOn) "PWR:1" else "PWR:0")
            btnPower.alpha = if (isPowerOn) 1.0f else 0.4f

            if (isPowerOn) {
                val savedBrt = prefs.getInt("brightness", 150)
                val savedClr = prefs.getInt("color", Color.WHITE)
                sendCommand("BRT:$savedBrt")
                sendColorCommand(savedClr)
            }
        }

        // --- СИНХРОНИЗАЦИЯ: КОЛЕСО -> КОЛЬЦО ---
        colorPicker?.setColorListener(ColorEnvelopeListener { envelope: ColorEnvelope, fromUser: Boolean ->
            val color = envelope.color
            currentCenterColor?.setCardBackgroundColor(color)

            if (fromUser) {
                val hue = getHueFromColor(color)
                circularSeekBar?.progress = hue
                circularSeekBar?.circleProgressColor = color
                resetEffectSelectionUI() // <- Вызываем сброс

                val currentTime = System.currentTimeMillis()
                if (currentTime - lastCommandTime > 60) {
                    sendColorCommand(color)
                    prefs.edit().putInt("color", color).apply()
                    lastCommandTime = currentTime
                }
            }
        })

        // --- СИНХРОНИЗАЦИЯ: КОЛЬЦО -> КОЛЕСО ---
        circularSeekBar?.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(circularSeekBar: CircularSeekBar?, progress: Float, fromUser: Boolean) {
                if (fromUser) {
                    val hsv = floatArrayOf(progress, 1f, 1f)
                    val color = Color.HSVToColor(hsv)

                    colorPicker?.setInitialColor(color)
                    currentCenterColor?.setCardBackgroundColor(color)
                    circularSeekBar?.circleProgressColor = color
                    resetEffectSelectionUI() // <- Вызываем сброс

                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastCommandTime > 60) {
                        sendColorCommand(color)
                        prefs.edit().putInt("color", color).apply()
                        lastCommandTime = currentTime
                    }
                }
            }
            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}
            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
        })

        // --- ЛОГИКА ЯРКОСТИ ---
        sbBrightness?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            // ... (остается без изменений)
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastCommandTime > 60) {
                        sendCommand("BRT:$progress")
                        lastCommandTime = currentTime
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val finalBrightness = seekBar?.progress ?: 0
                sendCommand("BRT:$finalBrightness")
                prefs.edit().putInt("brightness", finalBrightness).apply()
            }
        })

        // --- ЗАДАВАЕМЫЕ НАСТРОЙКИ (ВЕРХНИЕ ЦВЕТОВЫЕ КНОПКИ) ---
        setupPresetColor(view, R.id.btnColorRed, 255, 0, 0, currentCenterColor, colorPicker, circularSeekBar)
        setupPresetColor(view, R.id.btnColorGreen, 0, 255, 0, currentCenterColor, colorPicker, circularSeekBar)
        setupPresetColor(view, R.id.btnColorBlue, 0, 0, 255, currentCenterColor, colorPicker, circularSeekBar)
        setupPresetColor(view, R.id.btnColorPurple, 138, 43, 226, currentCenterColor, colorPicker, circularSeekBar)
        setupPresetColor(view, R.id.btnColorMagenta, 255, 0, 255, currentCenterColor, colorPicker, circularSeekBar)
        setupPresetColor(view, R.id.btnColorCyan, 0, 255, 255, currentCenterColor, colorPicker, circularSeekBar)

        // --- БЛОК "СТАНДАРТ" ---
        view.findViewById<MaterialCardView>(R.id.btnStandardLight)?.setOnClickListener {
            val white = Color.WHITE
            currentCenterColor?.setCardBackgroundColor(white)
            colorPicker?.setInitialColor(white)
            circularSeekBar?.progress = getHueFromColor(white)
            circularSeekBar?.circleProgressColor = white
            sendCommand("CLR:255,255,255")
            resetEffectSelectionUI() // <- Вызываем сброс
        }

        view.findViewById<MaterialCardView>(R.id.btnStandardRgb)?.setOnClickListener {
            sendCommand("EFF:1")
        }

        setupPresetColor(view, R.id.btnStandardCyan, 0, 255, 255, currentCenterColor, colorPicker, circularSeekBar)
        setupPresetColor(view, R.id.btnStandardRed, 255, 0, 0, currentCenterColor, colorPicker, circularSeekBar)
        setupPresetColor(view, R.id.btnStandardGreen, 0, 255, 0, currentCenterColor, colorPicker, circularSeekBar)
        setupPresetColor(view, R.id.btnStandardBlue, 0, 0, 255, currentCenterColor, colorPicker, circularSeekBar)
    }

    private fun setupPresetColor(view: View, buttonId: Int, r: Int, g: Int, b: Int, centerCircle: MaterialCardView?, colorPicker: ColorPickerView?, circularSeekBar: CircularSeekBar?) {
        view.findViewById<MaterialCardView>(buttonId)?.setOnClickListener {
            val color = Color.rgb(r, g, b)
            centerCircle?.setCardBackgroundColor(color)
            colorPicker?.setInitialColor(color)
            circularSeekBar?.progress = getHueFromColor(color)
            circularSeekBar?.circleProgressColor = color
            sendCommand("CLR:$r,$g,$b")
            prefs.edit().putInt("color", color).apply()

            prefs.edit().putInt("effect", 0).apply()
            resetEffectSelectionUI() // <- Вызываем сброс
        }
    }

    // ЗДЕСЬ МЫ ОТПРАВЛЯЕМ СИГНАЛ ВТОРОМУ ФРАГМЕНТУ!
    private fun resetEffectSelectionUI() {
        sharedViewModel.clearEffectSelectionEvent.value = true
    }

    private fun sendColorCommand(color: Int) {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        sendCommand("CLR:$r,$g,$b")
    }

    private fun sendCommand(cmd: String) {
        val fullCommand = "$cmd\n"
        sharedViewModel.bluetoothController?.sendMessage(fullCommand)
    }

    private fun getHueFromColor(color: Int): Float {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[0]
    }
}