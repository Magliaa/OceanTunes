package com.tunagold.oceantunes.ui.songsgrid

import SpacingItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tunagold.oceantunes.R

class SongsGridFragment : Fragment() {

    private lateinit var titleText: TextView

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
        titleText = view.findViewById(R.id.titlegrid)

        setSongsGridTitle("Titolo della griglia")

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

        view.findViewById<View>(R.id.btn_back).setOnClickListener {
            findNavController().popBackStack()
        }

        val sortText = view.findViewById<TextView>(R.id.sort_text)
        val sortArrow = view.findViewById<ImageView>(R.id.btn_sort)
        var isAscending = true
        var isSortEnabled = true

        fun applySortVisibility() {
            val visibility = if (isSortEnabled) View.VISIBLE else View.GONE
            sortText.visibility = visibility
            sortArrow.visibility = visibility
        }

        fun toggleSort() {
            if (!isSortEnabled) return

            isAscending = !isAscending
            sortArrow.animate()
                .rotation(if (isAscending) 0f else 180f)
                .setDuration(200)
                .start()
            sortText.text = if (isAscending) "Recenti" else "Da inizio"
        }

        fun setSortEnabled(enabled: Boolean) {
            isSortEnabled = enabled
            applySortVisibility()
        }

        applySortVisibility()
        sortText.setOnClickListener { toggleSort() }
        sortArrow.setOnClickListener { toggleSort() }
    }

    private fun setSongsGridTitle(text: String) {
        titleText.text = text
    }
}
