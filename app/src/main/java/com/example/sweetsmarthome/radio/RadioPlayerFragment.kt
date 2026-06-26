package com.example.sweetsmarthome.radio

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.sweetsmarthome.R
import com.google.android.material.snackbar.Snackbar
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

class RadioPlayerFragment : Fragment(R.layout.fragment_radio_player) {

    private lateinit var cover: ImageView
    private lateinit var playButton: ImageButton
    private lateinit var btNext: ImageButton
    private lateinit var btPrev: ImageButton
    private lateinit var btRandom: ImageButton
    private lateinit var rotateAnim: Animation
    private lateinit var tvTitleRadio: TextView
    private var rotateAnimator: ObjectAnimator? = null

    private val viewModel: RadioViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cover = view.findViewById(R.id.cover)
        playButton = view.findViewById(R.id.btPlay)
        btNext = view.findViewById(R.id.btNext)
        btPrev = view.findViewById(R.id.btPrev)
        btRandom = view.findViewById(R.id.btRandom)
        tvTitleRadio = view.findViewById(R.id.tvTitleRadio)
        val coverImage: ImageView = view.findViewById(R.id.cover)
        rotateAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)
        rotateAnimator = ObjectAnimator.ofFloat(coverImage, View.ROTATION, 0f, 360f).apply {
            duration = 10000 // Время одного полного оборота (10 секунд)
            repeatCount = ValueAnimator.INFINITE // Крутить бесконечно
            interpolator = LinearInterpolator() // Равномерная скорость (без ускорения/замедления)
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            if (isPlaying) {
                // Если играет, ставим иконку ПАУЗЫ
                playButton.setImageResource(R.drawable.ic_baseline_pause_24)

                // Возобновляем анимацию вращения
                if (rotateAnimator?.isPaused == true) {
                    rotateAnimator?.resume()
                } else if (rotateAnimator?.isStarted == false) {
                    rotateAnimator?.start()
                }
            } else {
                // Если на паузе, ставим иконку ПЛЕЙ
                playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)

                // Ставим анимацию на паузу
                rotateAnimator?.pause()
            }
        }

        viewModel.currentStation.observe(viewLifecycleOwner) { station ->
            tvTitleRadio.text = station.name
            cover.setImageResource(station.iconRadio)
            if (viewModel.isPlaying.value == true) {
                cover.startAnimation(rotateAnim)
            }
        }

        btNext.setOnClickListener { viewModel.next() }
        btPrev.setOnClickListener { viewModel.prev() }
        playButton.setOnClickListener { viewModel.togglePlayPause() }

        btRandom.setOnClickListener {
            viewModel.selectRandomStation()
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }
}
