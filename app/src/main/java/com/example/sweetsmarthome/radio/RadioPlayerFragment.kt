package com.example.sweetsmarthome.radio

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.sweetsmarthome.R
import com.google.android.material.snackbar.Snackbar

class RadioPlayerFragment : Fragment(R.layout.fragment_radio_player) {

    private lateinit var cover: ImageView
    private lateinit var playButton: ImageButton
    private lateinit var btNext: ImageButton
    private lateinit var btPrev: ImageButton
    private lateinit var rotateAnim: Animation

    private val viewModel: RadioViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cover = view.findViewById(R.id.cover)
        playButton = view.findViewById(R.id.btPlay)
        btNext = view.findViewById(R.id.btNext)
        btPrev = view.findViewById(R.id.btPrev)

        rotateAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate).apply {
            repeatCount = Animation.INFINITE
        }

        if (viewModel.isPlaying.value == true) {
            cover.startAnimation(rotateAnim)
        } else {
            cover.clearAnimation()
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { playing ->
            playButton.setImageResource(
                if (playing) R.drawable.ic_baseline_pause_24
                else R.drawable.ic_baseline_play_arrow_24
            )

            if (playing) {
                if (cover.animation == null || cover.animation.hasEnded()) {
                    cover.startAnimation(rotateAnim)
                }
            } else {
                cover.clearAnimation()
            }
        }

        viewModel.currentStation.observe(viewLifecycleOwner) { station ->
            cover.setImageResource(station.iconRadio)
            // Если радио играет — запуск анимации
            if (viewModel.isPlaying.value == true) {
                cover.startAnimation(rotateAnim)
            }
        }

        btNext.setOnClickListener { viewModel.next() }
        btPrev.setOnClickListener { viewModel.prev() }

        playButton.setOnClickListener {
            viewModel.togglePlayPause()
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }
}
