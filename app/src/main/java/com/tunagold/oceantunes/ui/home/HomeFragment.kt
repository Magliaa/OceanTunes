package com.tunagold.oceantunes.ui.home

import android.os.Bundle
import android.util.Log
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
import com.tunagold.oceantunes.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var nowSongsAdapter: CarouselAdapter<Song>
    private lateinit var recommendedSongsAdapter: CarouselAdapter<Song>

    // Variabili di stato locali per tenere traccia dei dati correnti delle canzoni
    private var currentTopFavoriteSongs: List<Song> = emptyList()
    private var currentTopRatedSongs: List<Song> = emptyList()

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
        Log.d("HomeFragment", "onViewCreated called.")

        nowSongsAdapter = CarouselAdapter<Song>(emptyList()) { item ->
            val clickedSong = item.third
            showSongDialog(clickedSong)
        }
        recommendedSongsAdapter = CarouselAdapter<Song>(emptyList()) { item ->
            val clickedSong = item.third
            showSongDialog(clickedSong)
        }

        binding.carouselNowSongs.adapter = nowSongsAdapter
        binding.carouselRecommended.adapter = recommendedSongsAdapter

        observeViewModel() // Imposta gli osservatori per i dati e gli stati di caricamento

        binding.nowMoreButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSongsGridFragment(
                songsListKey = currentTopFavoriteSongs.toTypedArray(),
                titleKey = getString(R.string.now_playing_title)
            )
            findNavController().navigate(action)
        }

        binding.recommendedMoreButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSongsGridFragment(
                songsListKey = currentTopRatedSongs.toTypedArray(),
                titleKey = getString(R.string.recommended_title)
            )
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        Log.d("HomeFragment", "observeViewModel called.")

        // Osserva i brani preferiti
        homeViewModel.topFavoriteSongs.observe(viewLifecycleOwner) { result ->
            Log.d("HomeFragment", "topFavoriteSongs observer triggered: $result")
            when (result) {
                is Result.Success -> {
                    currentTopFavoriteSongs = result.data ?: emptyList()
                    Log.d("HomeFragment", "topFavoriteSongs success, size: ${currentTopFavoriteSongs.size}")
                }
                is Result.Error -> {
                    currentTopFavoriteSongs = emptyList()
                    Log.e("HomeFragment", "topFavoriteSongs error: ${result.exception?.message}")
                    // Puoi mostrare un messaggio di errore all'utente qui
                }
                else -> {}
            }
            setupCarousels(nowPlaying = currentTopFavoriteSongs, recommended = currentTopRatedSongs)
        }

        // Osserva i brani più votati
        homeViewModel.topRatedSongs.observe(viewLifecycleOwner) { result ->
            Log.d("HomeFragment", "topRatedSongs observer triggered: $result")
            when (result) {
                is Result.Success -> {
                    currentTopRatedSongs = result.data ?: emptyList()
                    Log.d("HomeFragment", "topRatedSongs success, size: ${currentTopRatedSongs.size}")
                }
                is Result.Error -> {
                    currentTopRatedSongs = emptyList()
                    Log.e("HomeFragment", "topRatedSongs error: ${result.exception?.message}")
                    // Puoi mostrare un messaggio di errore all'utente qui
                }
                else -> {}
            }
            setupCarousels(nowPlaying = currentTopFavoriteSongs, recommended = currentTopRatedSongs)
        }

        // Osserva lo stato di caricamento per i brani preferiti
        homeViewModel.isLoadingTopFavoriteSongs.observe(viewLifecycleOwner) { isLoading ->
            Log.d("HomeFragment", "isLoadingTopFavoriteSongs: $isLoading")
            if (isLoading) {
                binding.progressBarNowSongs.visibility = View.VISIBLE
                binding.carouselNowSongs.visibility = View.GONE
            } else {
                binding.progressBarNowSongs.visibility = View.GONE
                // Applica l'animazione di fade-in
                binding.carouselNowSongs.alpha = 0f // Inizia con trasparenza
                binding.carouselNowSongs.visibility = View.VISIBLE
                binding.carouselNowSongs.animate()
                    .alpha(1f) // Anima a opacità completa
                    .setDuration(300) // Durata dell'animazione in ms
                    .start()
            }
            Log.d("HomeFragment", "progressBarNowSongs visibility: ${binding.progressBarNowSongs.visibility}, carouselNowSongs visibility: ${binding.carouselNowSongs.visibility}")
        }

        // Osserva lo stato di caricamento per i brani più votati
        homeViewModel.isLoadingTopRatedSongs.observe(viewLifecycleOwner) { isLoading ->
            Log.d("HomeFragment", "isLoadingTopRatedSongs: $isLoading")
            if (isLoading) {
                binding.progressBarRecommended.visibility = View.VISIBLE
                binding.carouselRecommended.visibility = View.GONE
            } else {
                binding.progressBarRecommended.visibility = View.GONE
                // Applica l'animazione di fade-in
                binding.carouselRecommended.alpha = 0f // Inizia con trasparenza
                binding.carouselRecommended.visibility = View.VISIBLE
                binding.carouselRecommended.animate()
                    .alpha(1f) // Anima a opacità completa
                    .setDuration(300) // Durata dell'animazione in ms
                    .start()
            }
            Log.d("HomeFragment", "progressBarRecommended visibility: ${binding.progressBarRecommended.visibility}, carouselRecommended visibility: ${binding.carouselRecommended.visibility}")
        }
    }

    private fun setupCarousels(nowPlaying: List<Song>, recommended: List<Song>) {
        Log.d("HomeFragment", "setupCarousels called. Now Playing size: ${nowPlaying.size}, Recommended size: ${recommended.size}")
        val nowPlayingDataForCarousel: List<Triple<String, String, Song>> = nowPlaying.map { song ->
            Triple(song.title, song.artists.joinToString(", "), song)
        }
        nowSongsAdapter.updateData(nowPlayingDataForCarousel)
        Log.d("HomeFragment", "nowSongsAdapter updated with size: ${nowPlayingDataForCarousel.size}")

        val recommendedDataForCarousel: List<Triple<String, String, Song>> = recommended.map { song ->
            Triple(song.title, song.artists.joinToString(", "), song)
        }
        recommendedSongsAdapter.updateData(recommendedDataForCarousel)
        Log.d("HomeFragment", "recommendedSongsAdapter updated with size: ${recommendedDataForCarousel.size}")
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