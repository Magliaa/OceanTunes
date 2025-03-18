package com.tunagold.oceantunes.ui.songsgrid
import SpacingItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tunagold.oceantunes.R

class SongsGridFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate il layout XML associato
        return inflater.inflate(R.layout.fragment_songsgrid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura il RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_songs)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        recyclerView.addItemDecoration(SpacingItemDecoration(spacingInPixels))
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Simula dati
        val songs = listOf(
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
        recyclerView.adapter = SongsAdapter(songs)

        // Imposta il click listener sul pulsante back
        val btnBack = view.findViewById<View>(R.id.btn_back)
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
