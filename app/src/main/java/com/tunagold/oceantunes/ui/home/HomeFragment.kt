package com.tunagold.oceantunes.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.databinding.FragmentHomeBinding
import com.tunagold.oceantunes.model.Song // Ensure this is your Parcelable Song model
import com.tunagold.oceantunes.ui.components.carousel.CarouselAdapter // Import the generic CarouselAdapter
import com.tunagold.oceantunes.utils.Result
import com.tunagold.oceantunes.ui.songsgrid.SongCardDialogFragment // Import SongCardDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    // Declare adapters as member variables to initialize once
    private lateinit var nowSongsAdapter: CarouselAdapter<Song>
    private lateinit var recommendedSongsAdapter: CarouselAdapter<Song>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize adapters here once with empty lists
        nowSongsAdapter = CarouselAdapter<Song>(emptyList()) { item ->
            val clickedSong = item.third // item.third is now directly of type Song
            showSongDialog(clickedSong)
        }
        recommendedSongsAdapter = CarouselAdapter<Song>(emptyList()) { item ->
            val clickedSong = item.third // item.third is now directly of type Song
            showSongDialog(clickedSong)
        }

        // Set the adapters to the RecyclerViews
        binding.carouselNowSongs.adapter = nowSongsAdapter
        binding.carouselRecommended.adapter = recommendedSongsAdapter


        observeViewModel()

        binding.nowMoreButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_songsGridFragment)
        }

        binding.recommendedMoreButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_songsGridFragment)
        }

        homeViewModel.fetchTopFavoriteSongs()
        homeViewModel.fetchTopRatedSongs()
    }

    private fun observeViewModel() {
        homeViewModel.topFavoriteSongs.observe(viewLifecycleOwner) { result ->
            val rated = (homeViewModel.topRatedSongs.value as? Result.Success)?.data ?: emptyList()
            when (result) {
                is Result.Loading -> { /* Keep existing content */ }
                is Result.Success -> {
                    setupCarousels(result.data ?: emptyList(), rated)
                }
                is Result.Error -> {
                    setupCarousels(emptyList(), rated)
                }
            }
        }

        homeViewModel.topRatedSongs.observe(viewLifecycleOwner) { result ->
            val favorites = (homeViewModel.topFavoriteSongs.value as? Result.Success)?.data ?: emptyList()
            when (result) {
                is Result.Loading -> { /* Keep existing content */ }
                is Result.Success -> {
                    setupCarousels(favorites, result.data ?: emptyList())
                }
                is Result.Error -> {
                    setupCarousels(favorites, emptyList())
                }
            }
        }
    }

    private fun setupCarousels(favorites: List<Song>, rated: List<Song>) {
        // Prepare data for the generic CarouselAdapter, passing the full Song object
        val favoritesItems: List<Triple<String, String, Song>> = favorites.map {
            Triple(it.title, it.artists.joinToString(", "), it) // Pass the entire Song object
        }

        val ratedItems: List<Triple<String, String, Song>> = rated.map {
            Triple(it.title, it.artists.joinToString(", "), it) // Pass the entire Song object
        }

        // Update the data in the existing adapter instances
        nowSongsAdapter.updateData(favoritesItems)
        recommendedSongsAdapter.updateData(ratedItems)
    }

    // Update showSongDialog to accept a Song object
    private fun showSongDialog(song: Song) {
        // Use the factory method from SongCardDialogFragment
        val dialog = SongCardDialogFragment.newInstance(song)
        dialog.show(parentFragmentManager, "SongCardDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}