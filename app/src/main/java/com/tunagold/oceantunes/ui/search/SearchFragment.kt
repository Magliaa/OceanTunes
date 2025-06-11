package com.tunagold.oceantunes.ui.search

import SpacingItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.databinding.FragmentSearchBinding
import com.tunagold.oceantunes.ui.songsgrid.SongCardDialogFragment
import com.tunagold.oceantunes.ui.songsgrid.SongsAdapter
import com.tunagold.oceantunes.model.Song
import com.tunagold.oceantunes.storage.room.SongRoom // Import SongRoom
import com.tunagold.oceantunes.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log // Import Log for debugging

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SongsAdapter
    private val searchViewModel: SearchViewModel by viewModels()

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

        // Initialize adapter with an empty list of Song objects.
        // The click listener now receives a Song object directly.
        adapter = SongsAdapter(emptyList()) { song ->
            Log.d("SongDebug", "SearchFragment: Song clicked. Type: ${song::class.java.name}, ID: ${song.id}")
            searchViewModel.addRecentSearch(Triple(song.title, song.artists.joinToString(", "), R.drawable.unknown_song_img))
            val dialog = SongCardDialogFragment.newInstance(song)
            dialog.show(parentFragmentManager, "song_card")
        }

        binding.recyclerViewSongs.adapter = adapter

        setupSearchBarListeners()
        setupClearRecentButton()
        observeViewModel()
    }

    private fun setupSearchBarListeners() {
        binding.searchBar.onClearAction = {
            binding.searchBar.editText?.setText("")
            searchViewModel.searchSongs("")
        }

        binding.searchBar.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchBar.editText?.text.isNullOrEmpty()) {
                searchViewModel.searchSongs("")
            }
        }

        binding.searchBar.editText?.addTextChangedListener { editable ->
            val query = editable?.toString().orEmpty()
            searchViewModel.searchSongs(query)
        }
    }

    private fun setupClearRecentButton() {
        binding.clearRecentButton.setOnClickListener {
            searchViewModel.clearRecentSearches()
        }
    }

    private fun observeViewModel() {
        searchViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.searchProgress.fadeIn()
                binding.recyclerViewSongs.fadeOut()
                binding.noResultsText.fadeOut()
                binding.recentSearchesLabel.fadeOut()
                binding.clearRecentButton.fadeOut()
            } else {
                binding.searchProgress.fadeOut()
            }
        }

        searchViewModel.searchResults.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val songs = result.data ?: emptyList()
                    // Directly pass List<Song> to the adapter
                    adapter.updateData(songs)
                    if (songs.isNotEmpty()) {
                        binding.recyclerViewSongs.fadeIn()
                        binding.noResultsText.fadeOut()
                    } else {
                        binding.recyclerViewSongs.fadeOut()
                        binding.noResultsText.text = searchViewModel.noResultsMessage.value!!.ifEmpty { "Nessun risultato trovato." }
                        binding.noResultsText.fadeIn()
                    }
                    binding.recentSearchesLabel.fadeOut()
                    binding.clearRecentButton.fadeOut()
                }
                is Result.Error -> {
                    adapter.updateData(emptyList()) // Pass empty list on error
                    binding.recyclerViewSongs.fadeOut()
                    binding.noResultsText.text = searchViewModel.noResultsMessage.value!!.ifEmpty { "Errore durante la ricerca." }
                    binding.noResultsText.fadeIn()
                    binding.recentSearchesLabel.fadeOut()
                    binding.clearRecentButton.fadeOut()
                }
                is Result.Loading -> {
                    // This state is primarily handled by the isLoading LiveData
                }
            }
        }

        searchViewModel.recentSearches.observe(viewLifecycleOwner) { recentTriples ->
            // Only update recent searches if search bar is empty AND not currently loading search results
            if (binding.searchBar.editText?.text.isNullOrEmpty() && searchViewModel.isLoading.value == false) {
                // Convert List<Triple<String, String, Int>> to List<SongRoom> for the adapter
                // This ensures the SongCardDialogFragment receives a Parcelable SongRoom object.
                val recentSongs = recentTriples.map { triple ->
                    SongRoom( // <--- Changed from Song to SongRoom
                        id = triple.first, // Assuming title is unique enough for ID or you have a real ID
                        title = triple.first,
                        artists = listOf(triple.second),
                        album = "Unknown", // Recent searches don't store album info
                        image = "", // Recent searches don't store image URLs directly, rely on default
                        releaseDate = "Unknown", // Recent searches don't store release date
                        credits = emptyList() // Recent searches don't store credits
                        // SongRoom constructor should have default values for ranking, avgScore etc.
                        // if they are not explicitly provided here.
                    )
                }
                adapter.updateData(recentSongs)
                if (recentSongs.isNotEmpty()) {
                    binding.recyclerViewSongs.fadeIn()
                    binding.recentSearchesLabel.fadeIn()
                    binding.clearRecentButton.fadeIn()
                    binding.noResultsText.fadeOut()
                } else {
                    binding.recyclerViewSongs.fadeOut()
                    binding.recentSearchesLabel.fadeOut()
                    binding.clearRecentButton.fadeOut()
                    binding.noResultsText.text = searchViewModel.noResultsMessage.value!!.ifEmpty { "Nessuna ricerca recente." }
                    binding.noResultsText.fadeIn()
                }
            }
        }

        searchViewModel.noResultsMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty() && searchViewModel.isLoading.value == false) {
                binding.noResultsText.text = message
                binding.noResultsText.fadeIn()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
