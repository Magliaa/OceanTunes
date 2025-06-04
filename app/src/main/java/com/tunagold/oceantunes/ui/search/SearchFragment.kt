package com.tunagold.oceantunes.ui.search

import SpacingItemDecoration
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.databinding.FragmentSearchBinding
import com.tunagold.oceantunes.ui.songsgrid.SongCardDialogFragment
import com.tunagold.oceantunes.ui.songsgrid.SongsAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SongsAdapter
    private val recentSongs = mutableListOf<Triple<String, String, Int>>()
    private var isSearching = false
    private val prefsKey = "recent_searches"
    private val handler = Handler(Looper.getMainLooper())

    // TODO: BACKEND - Rimuovi questo mock e usa i dati reali
    private val dummySongs = listOf(
        Triple("Blinding Lights", "The Weeknd", R.drawable.unknown_song_img),
        Triple("Levitating", "Dua Lipa", R.drawable.unknown_song_img),
        Triple("Save Your Tears", "The Weeknd", R.drawable.unknown_song_img),
        Triple("Peaches", "Justin Bieber", R.drawable.unknown_song_img),
        Triple("Montero", "Lil Nas X", R.drawable.unknown_song_img)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        binding.recyclerViewSongs.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewSongs.addItemDecoration(SpacingItemDecoration(spacingInPixels))

        adapter = SongsAdapter(emptyList()) { song ->
            if (isSearching) {
                val alreadyInRecent = recentSongs.any {
                    it.first == song.first && it.second == song.second && it.third == song.third
                }
                if (!alreadyInRecent) {
                    recentSongs.add(0, song)
                    if (recentSongs.size > 10) recentSongs.removeLast()
                    saveRecentSongs()
                }
                isSearching = false
            }

            val dialog = SongCardDialogFragment.newInstance(song)
            dialog.show(parentFragmentManager, "song_card")
        }

        binding.recyclerViewSongs.adapter = adapter

        loadRecentSongs()
        showRecentSearches()

        binding.searchBar.onClearAction = {
            isSearching = false
            binding.searchBar.editText?.setText("")
        }

        binding.searchBar.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchBar.editText?.text.isNullOrEmpty()) {
                isSearching = false
                showRecentSearches()
            }
        }

        binding.searchBar.editText?.addTextChangedListener { editable ->
            val query = editable?.toString()?.trim().orEmpty()

            handler.removeCallbacksAndMessages(null)

            if (query.isEmpty()) {
                isSearching = false
                showRecentSearches()
                return@addTextChangedListener
            }

            isSearching = true
            binding.searchProgress.fadeIn()

            binding.recyclerViewSongs.fadeOut()
            binding.noResultsText.fadeOut()
            binding.recentSearchesLabel.fadeOut()
            binding.clearRecentButton.fadeOut()

            handler.postDelayed({
                // TODO: BACKEND - Sostituire con chiamata API asincrona e parsing dei risultati
                val filtered = dummySongs.filter {
                    it.first.contains(query, ignoreCase = true) ||
                            it.second.contains(query, ignoreCase = true)
                }

                // TODO: BACKEND - Inserisci qui il callback per aggiornare l'adapter con i risultati veri
                adapter.updateData(filtered)

                binding.searchProgress.fadeOut()
                if (filtered.isNotEmpty()) {
                    binding.recyclerViewSongs.fadeIn()
                } else {
                    binding.noResultsText.text = "Nessun risultato trovato"
                    binding.noResultsText.fadeIn()
                }

            }, 1000) // TODO: BACKEND - Rimuovere delay quando usi risultati reali
        }

        binding.clearRecentButton.setOnClickListener {
            recentSongs.clear()
            saveRecentSongs()
            showRecentSearches()
        }
    }

    private fun showRecentSearches() {
        handler.removeCallbacksAndMessages(null)
        binding.searchProgress.fadeOut()
        if (recentSongs.isNotEmpty()) {
            adapter.updateData(recentSongs)
            binding.recyclerViewSongs.fadeIn()
            binding.noResultsText.fadeOut()
            binding.recentSearchesLabel.fadeIn()
            binding.clearRecentButton.fadeIn()
        } else {
            adapter.updateData(emptyList())
            binding.recyclerViewSongs.fadeOut()
            binding.noResultsText.text = "Nessuna ricerca recente"
            binding.noResultsText.fadeIn()
            binding.recentSearchesLabel.fadeOut()
            binding.clearRecentButton.fadeOut()
        }
    }

    private fun saveRecentSongs() {
        val prefs = requireContext().getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val strings = recentSongs.map { "${it.first}|${it.second}|${it.third}" }.toSet()
        prefs.edit {
            putStringSet(prefsKey, strings)
        }
    }

    private fun loadRecentSongs() {
        val prefs = requireContext().getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val strings = prefs.getStringSet(prefsKey, emptySet()) ?: emptySet()
        recentSongs.clear()
        strings.forEach { line ->
            val parts = line.split("|")
            if (parts.size == 3) {
                val title = parts[0]
                val artist = parts[1]
                val imageRes = parts[2].toIntOrNull() ?: R.drawable.unknown_song_img
                recentSongs.add(Triple(title, artist, imageRes))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }

    private fun View.fadeIn(duration: Long = 250) {
        if (visibility != View.VISIBLE) {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(duration).start()
        }
    }

    private fun View.fadeOut(duration: Long = 250) {
        if (visibility == View.VISIBLE) {
            animate().alpha(0f).setDuration(duration).withEndAction {
                visibility = View.GONE
            }.start()
        }
    }
}
