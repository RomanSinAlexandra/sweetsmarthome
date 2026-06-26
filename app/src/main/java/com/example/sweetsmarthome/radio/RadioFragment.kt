package com.example.sweetsmarthome.radio

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.viewpager2.widget.ViewPager2
import com.example.sweetsmarthome.R

class RadioFragment : Fragment(R.layout.fragment_radio) {

    private val viewModel: RadioViewModel by activityViewModels()
    private lateinit var viewPager: ViewPager2

    // Делаем плеер статичным (общим для всего приложения),
    // чтобы он переживал сворачивание приложения и переходы по вкладкам
    companion object {
        private var playerInstance: ExoPlayer? = null
    }

    // Удобный доступ к плееру
    private val player: ExoPlayer
        get() = playerInstance!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаем плеер ТОЛЬКО ОДИН РАЗ
        // Используем applicationContext, чтобы избежать утечек памяти
        if (playerInstance == null) {
            playerInstance = ExoPlayer.Builder(requireContext().applicationContext).build()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        viewPager.adapter = RadioPagerAdapter(this)

        viewModel.currentStation.observe(viewLifecycleOwner) { station ->
            val currentUrl = player.currentMediaItem?.localConfiguration?.uri?.toString()

            if (currentUrl != station.streamUrl) {
                val mediaItem = MediaItem.fromUri(station.streamUrl)
                player.setMediaItem(mediaItem)
                player.prepare()
            }
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { playing ->
            if (playing) {
                player.play()
            } else {
                player.pause()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}