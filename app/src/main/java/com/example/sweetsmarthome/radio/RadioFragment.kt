package com.example.sweetsmarthome.radio

import android.media.browse.MediaBrowser
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sweetsmarthome.R

class RadioFragment : Fragment(R.layout.fragment_radio) {

    private lateinit var player: ExoPlayer
    private lateinit var recyclerView: RecyclerView

    private val stations = RadioStations.list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        player = ExoPlayer.Builder(requireContext()).build()

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RadioAdapter(stations) { station ->
            playRadio(station.streamUrl)
        }
    }

    private fun playRadio(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
    }
}
