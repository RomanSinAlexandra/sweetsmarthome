package com.example.sweetsmarthome.led

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.sweetsmarthome.BluetoothSharedViewModel
import com.example.sweetsmarthome.R

class LedFragment : Fragment(R.layout.fragment_led_main), View.OnTouchListener {

    private lateinit var viewPager: ViewPager2

    private var xDown = 0f
    private var yDown = 0f
    private var touchSlop = 0

    // Идеальный современный способ подключения ViewModel!
    private val sharedViewModel: BluetoothSharedViewModel by activityViewModels()
    private var isPowerOn = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Настройка ViewPager2 ---
        viewPager = view.findViewById(R.id.viewPager)
        viewPager.adapter = LedPagerAdapter(this)
        viewPager.isUserInputEnabled = true

        touchSlop = ViewConfiguration.get(requireContext()).scaledTouchSlop

        // --- Настройка кнопки питания ---
        // Ищем кнопку внутри макета фрагмента (через view.)
        val btnPower: ImageView? = view.findViewById(R.id.btnPower)

        // Используем безопасный вызов btnPower?.
        // Код внутри выполнится ТОЛЬКО если кнопка физически находится в fragment_led_main.xml
        btnPower?.setOnClickListener {
            isPowerOn = !isPowerOn
            val colorState = if (isPowerOn) Color.GREEN else Color.GRAY
            btnPower.setColorFilter(colorState)

            val cmd = if (isPowerOn) "PWR:1\n" else "PWR:0\n"
            sharedViewModel.bluetoothController?.sendMessage(cmd)
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xDown = event.x
                yDown = event.y
                viewPager.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - xDown
                val dy = event.y - yDown

                val xDistSquare = dx * dx
                val yDistSquare = dy * dy
                val slopSquare = (touchSlop * touchSlop).toFloat()

                if (xDistSquare > yDistSquare && xDistSquare > slopSquare) {
                    // Явный горизонтальный свайп — разрешаем ViewPager листать страницы
                    viewPager.requestDisallowInterceptTouchEvent(false)
                } else {
                    // Круговое или вертикальное движение — блокируем ViewPager,
                    // чтобы кольцо и ползунки работали плавно
                    viewPager.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                viewPager.requestDisallowInterceptTouchEvent(false)
            }
        }
        return false
    }
}