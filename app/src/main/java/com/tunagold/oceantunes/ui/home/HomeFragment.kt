package com.tunagold.oceantunes.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.databinding.FragmentHomeBinding
import com.tunagold.oceantunes.ui.components.TopChartsSong
import com.tunagold.oceantunes.ui.components.carousel.CarouselAdapter
import com.tunagold.oceantunes.ui.components.carousel.MaterialCarousel
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavController
import com.tunagold.oceantunes.ui.songsgrid.SongCardDialogFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Popola i Top Charts
        populateTopCharts()

        // Configura i caroselli
        val materialCarousel: MaterialCarousel = view.findViewById(R.id.carouselNowSongs)
        val materialCarousel2: MaterialCarousel = view.findViewById(R.id.carouselRecommended)

        val items = listOf(
            Triple("Sonic 1", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 2", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 3", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 4", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 5", "SEGA", R.drawable.unknown_song_img)
        )
        val adapter = CarouselAdapter(items) { selectedItem ->
            val (title, artist, imgRes) = selectedItem as Triple<*, *, *>

            val dialog = SongCardDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title as? String ?: "")
                    putString("artist", artist as? String ?: "")
                    putInt("img", imgRes as? Int ?: R.drawable.unknown_song_img)
                }
            }
            dialog.show(parentFragmentManager, "SongCardDialog")
        }
        materialCarousel.adapter = adapter
        materialCarousel2.adapter = adapter

        // Configura il pulsante "Scopri di più"
        val nowMoreButton = view.findViewById<View>(R.id.nowMoreButton)
        nowMoreButton.setOnClickListener {
            // Naviga verso SongsGridFragment
            val action = R.id.action_homeFragment_to_songsGridFragment
            findNavController().navigate(action)

        }
    }

    private fun populateTopCharts() {
        val topChartsSongs = listOf(
            Triple("Blinding Lights", "The Weeknd", R.drawable.unknown_song_img),
            Triple("Levitating", "Dua Lipa", R.drawable.unknown_song_img),
            Triple("Save Your Tears", "The Weeknd", R.drawable.unknown_song_img),
            Triple("Peaches", "Justin Bieber", R.drawable.unknown_song_img),
            Triple("Montero", "Lil Nas X", R.drawable.unknown_song_img)
        )

        val topChartsViews = listOf(
            binding.root.findViewById<View>(R.id.topChartSong1) as TopChartsSong,
            binding.root.findViewById<View>(R.id.topChartSong2) as TopChartsSong,
            binding.root.findViewById<View>(R.id.topChartSong3) as TopChartsSong,
            binding.root.findViewById<View>(R.id.topChartSong4) as TopChartsSong,
            binding.root.findViewById<View>(R.id.topChartSong5) as TopChartsSong
        )

        topChartsSongs.zip(topChartsViews).forEach { (song, view) ->
            // Accesso diretto ai componenti del layout di TopChartsSong
            view.findViewById<ShapeableImageView>(R.id.topChartsSongImageID).setImageResource(song.third)
            view.findViewById<TextView>(R.id.topChartsSongTitleID).text = song.first
            view.findViewById<TextView>(R.id.topChartsSongArtistID).text = song.second
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
