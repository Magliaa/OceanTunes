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
import androidx.navigation.fragment.findNavController
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.databinding.FragmentProfileBinding
import com.tunagold.oceantunes.ui.components.carousel.CarouselAdapter
import com.tunagold.oceantunes.ui.songsgrid.SongCardDialogFragment
import android.content.Context

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Imposta animazione fade per il settings drawer
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
            EditProfileDialogFragment(currentName = "Mario Rossi") { name, imageUri ->
                binding.profileName.text = name
                imageUri?.let {
                    binding.profileImage.setImageURI(it)
                }
            }.show(parentFragmentManager, "EditProfileDialog")
        }

        binding.settingsCard.findViewById<View>(R.id.setting2).setOnClickListener {
            showNotificationSettingsDialog()
        }

        binding.settingsCard.findViewById<View>(R.id.setting3).setOnClickListener {
            // TODO: implement ToS
        }

        binding.settingsCard.findViewById<View>(R.id.setting4).setOnClickListener {
            // TODO: implement logout
        }

        setupCarousels()
        updateDataBox() // Mock popolamento

        val action = R.id.action_navigation_profile_to_songsGridFragment

        // Azione bottoni "Scopri tutto"
        binding.seeAllFavorites.setOnClickListener {
            findNavController().navigate(action)
        }

        binding.seeAllRated.setOnClickListener {
            findNavController().navigate(action)
        }



    }


    private fun showNotificationSettingsDialog() {
        val dialog = NotificationSettingsDialogFragment(
            trendingEnabled = true, // TODO: caricare da SharedPreferences
            topSongEnabled = false  // TODO: caricare da SharedPreferences
        ) { trending, topSong ->
            // TODO: salva le preferenze aggiornate
            val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean("notif_trending", trending)
                .putBoolean("notif_top_song", topSong)
                .apply()
        }

        dialog.show(parentFragmentManager, "NotificationSettingsDialog")
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
        // Mock data
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
