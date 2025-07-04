package com.tunagold.oceantunes.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.databinding.FragmentProfileBinding
import com.tunagold.oceantunes.ui.components.carousel.CarouselAdapter
import com.tunagold.oceantunes.ui.songsgrid.SongCardDialogFragment
import com.tunagold.oceantunes.utils.ToastHelper
import dagger.hilt.android.AndroidEntryPoint
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.utils.Result
import com.tunagold.oceantunes.ui.auth.AuthActivity
import com.tunagold.oceantunes.ui.profile.ProfileFragmentDirections


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var toastHelper: ToastHelper

    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var favoritesAdapter: CarouselAdapter<Song>
    private lateinit var ratedAdapter: CarouselAdapter<Song>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        toastHelper = ToastHelper(requireContext())
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsCard = requireView().findViewById<View>(R.id.settingsCard)
        settingsCard.alpha = 0f
        settingsCard.visibility = View.GONE

        binding.elipsesButton.setOnClickListener {
            toggleSettingsDrawer()
        }

        binding.scrollContainer.setOnTouchListener { _, event ->
            if (binding.settingsCard.isVisible &&
                (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE)) {
                hideSettingsDrawer()
            }
            false
        }

        binding.settingsCard.findViewById<View>(R.id.setting1).setOnClickListener {
            val currentUsername = (profileViewModel.currentUser.value as? Result.Success)?.data?.displayName ?: ""
            EditProfileDialogFragment(currentName = currentUsername) { newName, imageUri ->
                profileViewModel.updateUsername(newName)
                imageUri?.let {
                    binding.profileImage.setImageURI(it)
                }
            }.show(parentFragmentManager, "EditProfileDialog")
        }


        binding.settingsCard.findViewById<View>(R.id.setting4).setOnClickListener {
            profileViewModel.signOut()
        }

        binding.seeAllFavorites.setOnClickListener {
            val favoriteSongsList = (profileViewModel.favoriteSongs.value as? Result.Success)?.data ?: emptyList()
            val action = ProfileFragmentDirections.actionNavigationProfileToSongsGridFragment(
                songsListKey = favoriteSongsList.toTypedArray(),
                titleKey = getString(R.string.favorites_title)
            )
            findNavController().navigate(action)
        }

        binding.seeAllRated.setOnClickListener {
            val ratedSongsList = (profileViewModel.ratedSongs.value as? Result.Success)?.data ?: emptyList()
            val action = ProfileFragmentDirections.actionNavigationProfileToSongsGridFragment(
                songsListKey = ratedSongsList.toTypedArray(),
                titleKey = getString(R.string.rated_songs_title)
            )
            findNavController().navigate(action)
        }

        favoritesAdapter = CarouselAdapter<Song>(emptyList()) { item ->
            val clickedSong = item.third
            showSongDialog(clickedSong)
        }
        ratedAdapter = CarouselAdapter<Song>(emptyList()) { item ->
            val clickedSong = item.third
            showSongDialog(clickedSong)
        }

        binding.carouselFavorites.adapter = favoritesAdapter
        binding.carouselRated.adapter = ratedAdapter

        observeViewModel()
        profileViewModel.fetchCurrentUserDetails()
        profileViewModel.fetchUserInteractionStats()
        profileViewModel.fetchFavoriteSongs()
        profileViewModel.fetchRatedSongs()
    }

    private fun observeViewModel() {
        profileViewModel.currentUser.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.profileName.text = "Caricamento..."
                    binding.profileImage.alpha = 0.5f
                }
                is Result.Success -> {
                    result.data?.let { user ->
                        binding.profileName.text = user.displayName ?: "N/A"
                        binding.profileImage.alpha = 1.0f
                    } ?: run {
                        binding.profileName.text = "Guest"
                        binding.profileImage.alpha = 1.0f
                    }
                }
                is Result.Error -> {
                    binding.profileName.text = "Errore"
                    binding.profileImage.alpha = 1.0f
                    toastHelper.showShort("Errore caricamento dettagli utente: ${result.exception.message}")
                }
            }
        }

        profileViewModel.updateUsernameResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    toastHelper.showShort("Aggiornamento nome utente...")
                }
                is Result.Success -> {
                    toastHelper.showShort("Nome utente aggiornato con successo!")
                }
                is Result.Error -> {
                    toastHelper.showShort("Errore aggiornamento nome utente: ${result.exception.message}")
                }
            }
        }


        profileViewModel.signOutResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    toastHelper.showShort("Disconnessione in corso...")
                }
                is Result.Success -> {
                    toastHelper.showShort("Disconnessione effettuata con successo!")

                    navigateToAuthAndClearBackStack()
                }
                is Result.Error -> {
                    toastHelper.showShort("Errore disconnessione: ${result.exception.message}")
                }
            }
        }


        profileViewModel.userInteractionStats.observe(viewLifecycleOwner) { result ->

            when (result) {
                is Result.Loading -> {
                    binding.dataBox.findViewById<TextView>(R.id.val1)?.text = "..."
                    binding.dataBox.findViewById<TextView>(R.id.val2)?.text = "..."
                }
                is Result.Success -> {
                    result.data?.let { stats ->
                        binding.dataBox.findViewById<TextView>(R.id.val1)?.text = stats.totalFavoriteSongs.toString()
                        binding.dataBox.findViewById<TextView>(R.id.val2)?.text = stats.totalRatedSongs.toString()
                    } ?: run {
                        binding.dataBox.findViewById<TextView>(R.id.val1)?.text = "0"
                        binding.dataBox.findViewById<TextView>(R.id.val2)?.text = "0"
                    }
                }
                is Result.Error -> {
                    binding.dataBox.findViewById<TextView>(R.id.val1)?.text = "N/A"
                    binding.dataBox.findViewById<TextView>(R.id.val2)?.text = "N/A"
                    toastHelper.showShort("Errore caricamento statistiche: ${result.exception.message}")
                }
            }
        }

        profileViewModel.favoriteSongs.observe(viewLifecycleOwner) { result ->
            if (result is Result.Success) {
                val currentRatedSongs = (profileViewModel.ratedSongs.value as? Result.Success)?.data ?: emptyList()
                setupCarousels(favorites = result.data ?: emptyList(), rated = currentRatedSongs)
            } else if (result is Result.Error) {
                toastHelper.showShort("Errore caricamento canzoni preferite: ${result.exception.message}")
                val currentRatedSongs = (profileViewModel.ratedSongs.value as? Result.Success)?.data ?: emptyList()
                setupCarousels(favorites = emptyList(), rated = currentRatedSongs)
            }
        }

        profileViewModel.ratedSongs.observe(viewLifecycleOwner) { result ->
            if (result is Result.Success) {
                val currentFavoriteSongs = (profileViewModel.favoriteSongs.value as? Result.Success)?.data ?: emptyList()
                setupCarousels(favorites = currentFavoriteSongs, rated = result.data ?: emptyList())
            } else if (result is Result.Error) {
                toastHelper.showShort("Errore caricamento canzoni valutate: ${result.exception.message}")
                val currentFavoriteSongs = (profileViewModel.favoriteSongs.value as? Result.Success)?.data ?: emptyList()
                setupCarousels(favorites = currentFavoriteSongs, rated = emptyList())
            }
        }

        profileViewModel.isLoadingFavoriteSongs.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBarFavorites.visibility = View.VISIBLE
                binding.carouselFavorites.visibility = View.GONE
            } else {
                binding.progressBarFavorites.visibility = View.GONE
                binding.carouselFavorites.alpha = 0f
                binding.carouselFavorites.visibility = View.VISIBLE
                binding.carouselFavorites.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
        }

        profileViewModel.isLoadingRatedSongs.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBarRated.visibility = View.VISIBLE
                binding.carouselRated.visibility = View.GONE
            } else {
                binding.progressBarRated.visibility = View.GONE
                binding.carouselRated.alpha = 0f
                binding.carouselRated.visibility = View.VISIBLE
                binding.carouselRated.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
        }
    }

    private fun toggleSettingsDrawer() {
        val settingsCard = requireView().findViewById<View>(R.id.settingsCard)
        if (settingsCard.visibility == View.VISIBLE) {
            hideSettingsDrawer()
        } else {
            settingsCard.alpha = 0f
            settingsCard.visibility = View.VISIBLE
            settingsCard.animate().alpha(1f).setDuration(200).start()
        }
    }

    private fun hideSettingsDrawer() {
        val settingsCard = requireView().findViewById<View>(R.id.settingsCard)
        settingsCard.animate().alpha(0f).setDuration(200).withEndAction {
            settingsCard.visibility = View.GONE
        }.start()
    }

    private fun animateFadeOutThenNavigate(view: View, destinationId: Int) {
        view.animate().alpha(0f).setDuration(300).withEndAction {
            findNavController().navigate(destinationId)
            view.alpha = 1f
        }.start()
    }

    private fun setupCarousels(favorites: List<Song>, rated: List<Song>) {
        val favoritesDataForCarousel: List<Triple<String, String, Song>> = favorites.map { song ->
            Triple(song.title, song.artists.joinToString(", "), song)
        }

        val ratedDataForCarousel: List<Triple<String, String, Song>> = rated.map { song ->
            Triple(song.title, song.artists.joinToString(", "), song)
        }

        favoritesAdapter.updateData(favoritesDataForCarousel)
        ratedAdapter.updateData(ratedDataForCarousel)
    }

    private fun showSongDialog(song: Song) {
        val dialog = SongCardDialogFragment.newInstance(song)
        dialog.show(parentFragmentManager, "SongCardDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToAuthAndClearBackStack() {
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finish()
    }
}
