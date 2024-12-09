package com.tunagold.oceantunes.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.slider.Slider
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.databinding.FragmentProfileBinding
import com.tunagold.oceantunes.ui.components.carousel.CarouselAdapter
import com.tunagold.oceantunes.ui.components.carousel.MaterialCarousel


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root




        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val materialCarousel: MaterialCarousel = view.findViewById(R.id.my_carousel)

        //insert items in the carousel with API
        val items = listOf(
            "Sonic 1",
            "Sonic 2",
            "Sonic 3",
            "Sonic 4",
            "Sonic 5"
        )
        val adapter = CarouselAdapter(items)
        materialCarousel.adapter = adapter

        val slider = view.findViewById<Slider>(R.id.material_slider)

        slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
            }

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped
            }
        })

        slider.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
        }

        slider.setLabelFormatter { value: Float ->
            String.format("%.0f★", value)
        }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}