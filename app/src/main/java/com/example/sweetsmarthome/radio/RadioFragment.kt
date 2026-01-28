package com.example.sweetsmarthome.radio

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.viewpager2.widget.ViewPager2
import com.example.sweetsmarthome.R

class RadioFragment : Fragment(R.layout.fragment_radio) {

    private val viewModel: RadioViewModel by activityViewModels()
    private lateinit var player: ExoPlayer
    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player = ExoPlayer.Builder(requireContext()).build()

        viewPager = view.findViewById(R.id.viewPager)
        viewPager.adapter = RadioPagerAdapter(this)

        viewModel.currentStation.observe(viewLifecycleOwner) { station ->
            val mediaItem = MediaItem.fromUri(station.streamUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { playing ->
            if (playing) player.play() else player.pause()
        }
    }

    fun getPlayer(): ExoPlayer = player

    private fun playRadio(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

}
