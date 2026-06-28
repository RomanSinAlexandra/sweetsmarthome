package com.example.sweetsmarthome.radio

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sweetsmarthome.R

class RadioListFragment : Fragment(R.layout.fragment_radio_list) {

    private val viewModel: RadioViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rcRadio)
        val etSearch = view.findViewById<EditText>(R.id.etSearchRadio)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        Log.d("MyLog", "EditText найден? ${etSearch != null}")
        val adapter = RadioAdapter(RadioStations.list) { station ->
            viewModel.onStationClicked(station)
        }
        recyclerView.adapter = adapter

        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                adapter.filter(s.toString())
            }
        })

        // Наблюдаем за станцией И состоянием воспроизведения одновременно
        viewModel.currentStation.observe(viewLifecycleOwner) { station ->
            adapter.setStatus(station, viewModel.isPlaying.value ?: false)
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            adapter.setStatus(viewModel.currentStation.value, isPlaying)
        }
    }
}
