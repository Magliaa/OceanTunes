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
import com.tunagold.oceantunes.storage.room.SongRoom
import com.tunagold.oceantunes.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log

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

        adapter = SongsAdapter(emptyList()) { song ->
            Log.d("SongDebug", "SearchFragment: Song clicked. Type: ${song::class.java.name}, ID: ${song.id}")
            val imageResId = R.drawable.unknown_song_img
            searchViewModel.addRecentSearch(Triple(song.title, song.artists.joinToString(", "), imageResId))
            val dialog = SongCardDialogFragment.newInstance(song)
            dialog.show(parentFragmentManager, "song_card")
        }

        binding.recyclerViewSongs.adapter = adapter

        setupSearchBarListeners()
        setupClearRecentButton()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        updateUiState()
    }

    private fun setupSearchBarListeners() {
        binding.searchBar.onClearAction = {
            binding.searchBar.editText?.setText("")
            searchViewModel.searchSongs("")
        }

        binding.searchBar.editText?.setOnFocusChangeListener { _, hasFocus ->
            val currentQueryIsEmpty = binding.searchBar.editText?.text.isNullOrEmpty()
            if (hasFocus && currentQueryIsEmpty) {
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
            searchViewModel.searchSongs("")
        }
    }

    private fun observeViewModel() {
        searchViewModel.isLoading.observe(viewLifecycleOwner) { updateUiState() }
        searchViewModel.searchResults.observe(viewLifecycleOwner) { updateUiState() }
        searchViewModel.recentSearches.observe(viewLifecycleOwner) { updateUiState() }
        searchViewModel.noResultsMessage.observe(viewLifecycleOwner) { updateUiState() }
    }

    private fun updateUiState() {
        val isLoading = searchViewModel.isLoading.value ?: false
        val currentQuery = binding.searchBar.editText?.text?.toString().orEmpty()
        val searchResults = searchViewModel.searchResults.value
        val recentSearches = searchViewModel.recentSearches.value.orEmpty()
        val noResultsMessage = searchViewModel.noResultsMessage.value.orEmpty()
        var recentOpen = true;

        binding.searchProgress.visibility = View.GONE
        binding.recyclerViewSongs.visibility = View.GONE
        binding.noResultsText.visibility = View.GONE
        binding.recentSearchesLabel.visibility = View.GONE
        binding.clearRecentButton.visibility = View.GONE

        if (isLoading) {
            binding.searchProgress.fadeIn()
        } else {
            if (currentQuery.isNotEmpty()) {
                when (searchResults) {
                    is Result.Success -> {
                        val songs = searchResults.data
                        if (songs.isNotEmpty()) {
                            adapter.updateData(songs)
                            binding.recyclerViewSongs.fadeIn()
                        } else {
                            binding.noResultsText.text = noResultsMessage.ifEmpty { "Nessun risultato trovato." }
                            binding.noResultsText.fadeIn()
                        }
                    }
                    is Result.Error -> {
                        binding.noResultsText.text = noResultsMessage.ifEmpty { "Errore durante la ricerca." }
                        binding.noResultsText.fadeIn()
                    }
                    is Result.Loading -> { }
                    null -> { }
                }
            } else {
                if (recentSearches.isNotEmpty() && recentOpen) {
                    adapter.updateData(recentSearches.map { triple ->
                        SongRoom(
                            id = triple.first,
                            title = triple.first,
                            artists = listOf(triple.second),
                            album = "Unknown",
                            image = "",
                            releaseDate = "Unknown",
                            credits = emptyList()
                        )
                    })
                    binding.recyclerViewSongs.fadeIn()
                    binding.recentSearchesLabel.fadeIn()
                    binding.clearRecentButton.fadeIn()
                } else {
                    binding.noResultsText.text = "Nessuna ricerca recente."
                    binding.noResultsText.fadeIn()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun View.fadeIn(duration: Long = 250) {
        this.clearAnimation()
        if (visibility != View.VISIBLE || alpha < 1f) {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(duration).start()
        }
    }

    private fun View.fadeOut(duration: Long = 250) {
        this.clearAnimation()
        if (visibility == View.VISIBLE || alpha > 0f) {
            animate().alpha(0f).setDuration(duration).withEndAction {
                visibility = View.GONE
            }.start()
        }
    }
}