package com.example.sweetsmarthome

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bt_def.bluetooth.BluetoothController
import kotlinx.coroutines.*

class BluetoothSharedViewModel : ViewModel() {
    var bluetoothController: BluetoothController? = null
    val clearEffectSelectionEvent = MutableLiveData<Boolean>()

    // Переносим корутины и джобы внутрь класса
    private var audioJob: Job? = null
    private val viewModelScopeCustom = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @SuppressLint("MissingPermission") // ВАЖНО: Разрешение RECORD_AUDIO должно быть запрошено в Activity/Fragment до вызова!
    fun startAudioStreaming() {
        if (audioJob?.isActive == true) return

        audioJob = viewModelScopeCustom.launch {
            val bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

            // Если система не может выделить буфер, прерываемся
            if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) return@launch

            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            val buffer = ShortArray(bufferSize)

            try {
                audioRecord.startRecording()

                while (isActive) {
                    val read = audioRecord.read(buffer, 0, bufferSize)
                    if (read > 0) {
                        var sum = 0L
                        for (i in 0 until read) {
                            sum += Math.abs(buffer[i].toInt())
                        }

                        // Средняя амплитуда звука
                        val average = sum / read

                        // Снижаем делитель с 50 до 5 (поиграйся с этой цифрой!)
                        // Вычитаем 10, чтобы убрать базовый "белый шум" микрофона в тишине
                        var volume = (average / 5) - 10

                        if (volume < 0) volume = 0 // Защита от отрицательных чисел

                        val finalVolume = volume.coerceIn(0, 255)

                        // РАСКОММЕНТИРУЙ, чтобы увидеть звук в Logcat:
                        Log.d("AudioStream", "Отправляем звук: $finalVolume")

                        // Отправляем данные на ESP32
                        bluetoothController?.sendMessage("SND:${finalVolume}\n")
                    }
                    delay(100)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Гарантированно освобождаем микрофон при отмене корутины
                try {
                    audioRecord.stop()
                    audioRecord.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun stopAudioStreaming() {
        audioJob?.cancel()
    }

    // Вызывается автоматически, когда ViewModel уничтожается
    override fun onCleared() {
        super.onCleared()
        stopAudioStreaming()
        viewModelScopeCustom.cancel() // Отменяем все фоновые процессы
    }
}