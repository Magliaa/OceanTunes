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
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.ui.components.carousel.CarouselAdapter
import com.tunagold.oceantunes.utils.Result
import com.tunagold.oceantunes.ui.songsgrid.SongCardDialogFragment
// Import the generated Safe Args directions
import com.tunagold.oceantunes.ui.home.HomeFragmentDirections
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

        // --- MODIFIED NAVIGATION CALLS ---
        binding.nowMoreButton.setOnClickListener {
            val nowPlayingSongsList = (homeViewModel.topFavoriteSongs.value as? Result.Success)?.data ?: emptyList()
            val action = HomeFragmentDirections.actionHomeFragmentToSongsGridFragment(
                songsListKey = nowPlayingSongsList.toTypedArray(), // Pass as array
                titleKey = getString(R.string.now_playing_title) // Define a string resource for title
            )
            findNavController().navigate(action)
        }

        binding.recommendedMoreButton.setOnClickListener {
            val recommendedSongsList = (homeViewModel.topRatedSongs.value as? Result.Success)?.data ?: emptyList()
            val action = HomeFragmentDirections.actionHomeFragmentToSongsGridFragment(
                songsListKey = recommendedSongsList.toTypedArray(), // Pass as array
                titleKey = getString(R.string.recommended_title) // Define a string resource for title
            )
            findNavController().navigate(action)
        }
        // --- END MODIFIED NAVIGATION CALLS ---

        homeViewModel.fetchTopFavoriteSongs()
        homeViewModel.fetchTopRatedSongs()
    }

    private fun observeViewModel() {
        homeViewModel.topFavoriteSongs.observe(viewLifecycleOwner) { result ->
            if (result is Result.Success) {
                val currentNowPlayingSongs = (homeViewModel.topFavoriteSongs.value as? Result.Success)?.data ?: emptyList()
                setupCarousels(nowPlaying = currentNowPlayingSongs, recommended = result.data ?: emptyList())
            } else if (result is Result.Error) {
                // Handle error or show toast
                val currentNowPlayingSongs = (homeViewModel.topFavoriteSongs.value as? Result.Success)?.data ?: emptyList()
                setupCarousels(nowPlaying = currentNowPlayingSongs, recommended = emptyList())
            }
        }

        homeViewModel.topRatedSongs.observe(viewLifecycleOwner) { result ->
            if (result is Result.Success) {
                val currentRecommendedSongs = (homeViewModel.topRatedSongs.value as? Result.Success)?.data ?: emptyList()
                setupCarousels(nowPlaying = result.data ?: emptyList(), recommended = currentRecommendedSongs)
            } else if (result is Result.Error) {
                // Handle error or show toast
                val currentRecommendedSongs = (homeViewModel.topRatedSongs.value as? Result.Success)?.data ?: emptyList()
                setupCarousels(nowPlaying = emptyList(), recommended = currentRecommendedSongs)
            }
        }
    }

    private fun setupCarousels(nowPlaying: List<Song>, recommended: List<Song>) {
        val nowPlayingDataForCarousel: List<Triple<String, String, Song>> = nowPlaying.map { song ->
            Triple(song.title, song.artists.joinToString(", "), song)
        }

        val recommendedDataForCarousel: List<Triple<String, String, Song>> = recommended.map { song ->
            Triple(song.title, song.artists.joinToString(", "), song)
        }

        nowSongsAdapter.updateData(nowPlayingDataForCarousel)
        recommendedSongsAdapter.updateData(recommendedDataForCarousel)
    }

    private fun showSongDialog(song: Song) {
        val dialog = SongCardDialogFragment.newInstance(song)
        dialog.show(parentFragmentManager, "SongCardDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}