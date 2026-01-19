package com.example.sweetsmarthome

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sweetsmarthome.databinding.FragmentRadioBinding

class RadioFragment : Fragment() {

    private var _binding: FragmentRadioBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    //private val radioUrl = "https://streaming.brol.tech/rtfmlounge"
    private val radioUrl = "https://listen.moe/stream"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRadioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playButton.setOnClickListener {
            playRadio()
        }

        binding.stopButton.setOnClickListener {
            stopRadio()
        }
    }

    private fun playRadio() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {

                setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                        .build()
                )

                setDataSource(radioUrl)
                setOnPreparedListener { start() }
                prepareAsync()
            }
        }
    }

    private fun stopRadio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
