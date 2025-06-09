package com.tunagold.oceantunes.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
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
import android.content.Context
import com.tunagold.oceantunes.utils.ToastHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var toastHelper: ToastHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toastHelper = ToastHelper(requireContext())

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
            // Get current username from ViewModel to pre-fill the dialog
            val currentUsername = profileViewModel.currentUser.value?.displayName ?: ""
            EditProfileDialogFragment(currentName = currentUsername) { newName, imageUri ->
                profileViewModel.updateUsername(newName)
                imageUri?.let {
                    binding.profileImage.setImageURI(it)
                }
            }.show(parentFragmentManager, "EditProfileDialog")
        }

        binding.settingsCard.findViewById<View>(R.id.setting4).setOnClickListener {
            // Implement logout
            profileViewModel.signOut()
        }

        setupCarousels()
        // No longer using mock data for updateDataBox directly
        // updateDataBox()

        val action = R.id.action_navigation_profile_to_songsGridFragment

        binding.seeAllFavorites.setOnClickListener {
            findNavController().navigate(action)
        }

        binding.seeAllRated.setOnClickListener {
            findNavController().navigate(action)
        }

        observeViewModel() // Observe ViewModel LiveData
        profileViewModel.fetchCurrentUserDetails() // Fetch user details on start
    }

    private fun observeViewModel() {
        profileViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.profileName.text = it.displayName
            } ?: run {
                binding.profileName.text = "Guest"
            }
        }

        profileViewModel.updateUsernameResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                toastHelper.showShort("Nome utente aggiornato con successo!")
            }.onFailure {
                toastHelper.showShort("Errore aggiornamento nome utente: ${it.message}")
            }
        }

        profileViewModel.signOutResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                toastHelper.showShort("Disconnessione effettuata con successo!")
                // Navigate back to the authentication activity (LoginFragment)
                findNavController().navigate(R.id.auth_nav_graph) // This will navigate to the AuthActivity and its start destination (LoginFragment)
            }.onFailure {
                toastHelper.showShort("Errore disconnessione: ${it.message}")
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

    private fun updateDataBox() {
        val favoriteCount = 342
        val ratedCount = 321

        binding.dataBox.findViewById<TextView>(R.id.val1)?.text = favoriteCount.toString()
        binding.dataBox.findViewById<TextView>(R.id.val2)?.text = ratedCount.toString()
    }

    private fun setupCarousels() {
        val favorites = listOf(
            Triple("Sonic 1", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 2", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 3", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 4", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 5", "SEGA", R.drawable.unknown_song_img)
        )

        val rated = listOf(
            Triple("Sonic 1", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 2", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 3", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 4", "SEGA", R.drawable.unknown_song_img),
            Triple("Sonic 5", "SEGA", R.drawable.unknown_song_img)
        )

        val favoritesAdapter = CarouselAdapter(favorites) { item ->
            val (title, artist, imgRes) = item as Triple<*, *, *>
            showSongDialog(title, artist, imgRes)
        }

        val ratedAdapter = CarouselAdapter(rated) { item ->
            val (title, artist, imgRes) = item as Triple<*, *, *>
            showSongDialog(title, artist, imgRes)
        }

        binding.carouselFavorites.adapter = favoritesAdapter
        binding.carouselRated.adapter = ratedAdapter
    }

    private fun showSongDialog(title: Any?, artist: Any?, imgRes: Any?) {
        val dialog = SongCardDialogFragment().apply {
            arguments = Bundle().apply {
                putString("title", title as? String ?: "")
                putString("artist", artist as? String ?: "")
                putInt("img", imgRes as? Int ?: R.drawable.unknown_song_img)
            }
        }
        dialog.show(parentFragmentManager, "SongCardDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
