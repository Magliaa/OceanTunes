package com.tunagold.oceantunes.ui.search

import SpacingItemDecoration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.databinding.FragmentSearchBinding
import com.tunagold.oceantunes.ui.songsgrid.SongsAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SongsAdapter

    private val dummySongs = listOf(
        Triple("Blinding Lights", "The Weeknd", R.drawable.unknown_song_img),
        Triple("Levitating", "Dua Lipa", R.drawable.unknown_song_img),
        Triple("Save Your Tears", "The Weeknd", R.drawable.unknown_song_img),
        Triple("Peaches", "Justin Bieber", R.drawable.unknown_song_img),
        Triple("Montero", "Lil Nas X", R.drawable.unknown_song_img),
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

        adapter = SongsAdapter(emptyList())
        binding.recyclerViewSongs.adapter = adapter

        binding.noResultsText.visibility = View.GONE
        binding.searchProgress.alpha = 0f
        binding.searchProgress.visibility = View.GONE

        binding.searchBar.editText?.addTextChangedListener { editable ->
            val query = editable.toString()

            // Fade in della progress bar
            binding.searchProgress.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(300).start()
            }

            // Nascondi lista e messaggio durante il caricamento
            binding.recyclerViewSongs.visibility = View.GONE
            binding.noResultsText.visibility = View.GONE

            Handler(Looper.getMainLooper()).postDelayed({
                val filtered = dummySongs.filter {
                    it.first.contains(query, ignoreCase = true) ||
                            it.second.contains(query, ignoreCase = true)
                }

                adapter.updateData(filtered)

                // Fade out progress bar
                binding.searchProgress.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        binding.searchProgress.visibility = View.GONE

                        binding.recyclerViewSongs.visibility =
                            if (filtered.isNotEmpty()) View.VISIBLE else View.GONE
                        binding.noResultsText.visibility =
                            if (filtered.isEmpty()) View.VISIBLE else View.GONE
                    }.start()

            }, 1000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
