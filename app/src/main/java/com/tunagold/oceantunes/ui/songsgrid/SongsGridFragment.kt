package com.tunagold.oceantunes.ui.songsgrid

import SpacingItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Import viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.model.Song // Import Song model
import dagger.hilt.android.AndroidEntryPoint // Import AndroidEntryPoint

@AndroidEntryPoint // Add Hilt annotation
class SongsGridFragment : Fragment() {

    private lateinit var titleText: TextView
    private lateinit var adapter: SongsAdapter // Change to SongsAdapter
    private val songsGridViewModel: SongsGridViewModel by viewModels() // Inject ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_songsgrid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleText = view.findViewById(R.id.titlegrid)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_songs)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        recyclerView.addItemDecoration(SpacingItemDecoration(spacingInPixels))
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = SongsAdapter(emptyList()) { song -> // Initialize with empty list
            val dialog = SongCardDialogFragment.newInstance(song)
            dialog.show(parentFragmentManager, "song_card")
        }
        recyclerView.adapter = adapter

        view.findViewById<View>(R.id.btn_back).setOnClickListener {
            findNavController().popBackStack()
        }

        val sortText = view.findViewById<TextView>(R.id.sort_text)
        val sortArrow = view.findViewById<ImageView>(R.id.btn_sort)

        sortText.setOnClickListener { songsGridViewModel.toggleSortOrder() }
        sortArrow.setOnClickListener { songsGridViewModel.toggleSortOrder() }

        observeViewModel()

        // Set initial title and songs from arguments
        // These calls will be handled by the ViewModel's init block via SavedStateHandle
        // No direct calls needed here if data is passed via navigation arguments.
    }

    private fun observeViewModel() {
        songsGridViewModel.title.observe(viewLifecycleOwner) { title ->
            titleText.text = title
        }

        songsGridViewModel.songs.observe(viewLifecycleOwner) { songsList ->
            adapter.updateData(songsList)
        }

        songsGridViewModel.isAscending.observe(viewLifecycleOwner) { isAscending ->
            val sortText = requireView().findViewById<TextView>(R.id.sort_text)
            val sortArrow = requireView().findViewById<ImageView>(R.id.btn_sort)
            sortArrow.animate()
                .rotation(if (isAscending) 0f else 180f)
                .setDuration(200)
                .start()
            sortText.text = if (isAscending) "Recenti" else "Da inizio"
        }
    }
}
