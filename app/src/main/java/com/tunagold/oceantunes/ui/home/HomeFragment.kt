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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

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
                is Result.Loading -> { }
                is Result.Success -> {
                    setupCarousels(result.data, rated)
                }
                is Result.Error -> {
                    setupCarousels(emptyList(), rated)
                }
            }
        }

        homeViewModel.topRatedSongs.observe(viewLifecycleOwner) { result ->
            val favorites = (homeViewModel.topFavoriteSongs.value as? Result.Success)?.data ?: emptyList()
            when (result) {
                is Result.Loading -> { }
                is Result.Success -> {
                    setupCarousels(favorites, result.data)
                }
                is Result.Error -> {
                    setupCarousels(favorites, emptyList())
                }
            }
        }
    }

    private fun setupCarousels(favorites: List<Song>, rated: List<Song>) {
        val favoritesItems = favorites.map {
            Triple(it.title, it.artists.joinToString(", "), it.image)
        }

        val ratedItems = rated.map {
            Triple(it.title, it.artists.joinToString(", "), it.image)
        }

        binding.carouselNowSongs.adapter = CarouselAdapter(favoritesItems) { item ->
            val (title, artist, imageUrl) = item
            showSongDialog(title, artist, imageUrl)
        }

        binding.carouselRecommended.adapter = CarouselAdapter(ratedItems) { item ->
            val (title, artist, imageUrl) = item
            showSongDialog(title, artist, imageUrl)
        }
    }

    private fun showSongDialog(title: String?, artist: String?, imageUrl: String?) {
        val dialog = SongCardDialogFragment().apply {
            arguments = Bundle().apply {
                putString("title", title ?: "")
                putString("artist", artist ?: "")
                putString("imageUrl", imageUrl ?: "")
            }
        }
        dialog.show(parentFragmentManager, "SongCardDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}