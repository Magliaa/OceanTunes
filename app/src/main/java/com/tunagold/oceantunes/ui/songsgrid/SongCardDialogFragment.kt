package com.tunagold.oceantunes.ui.songsgrid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.model.Song // Keep importing Song
import com.tunagold.oceantunes.storage.room.SongRoom // You might still need SongRoom for Room operations elsewhere
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import android.widget.TextView
import com.tunagold.oceantunes.ui.components.MaterialRating
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SongCardDialogFragment : DialogFragment() {

    private var isDismissing = false
    private val viewModel: SongCardDialogViewModel by viewModels()

    companion object {
        private const val ARG_SONG = "song_object"
        fun newInstance(song: Song): SongCardDialogFragment {
            val fragment = SongCardDialogFragment()
            val args = Bundle().apply {
                // --- FIX IS HERE ---
                // Pass the Song object directly, as it is Parcelable
                putParcelable(ARG_SONG, song)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SongCardDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_songcard, container, false)
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        val card = dialog?.findViewById<View>(R.id.song_card_complex)
        val screenHeight = resources.displayMetrics.heightPixels
        card?.layoutParams?.height = (screenHeight * 0.85).toInt()
        card?.requestLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- FIX IS HERE ---
        // Retrieve the Song object directly
        val song = arguments?.getParcelable<Song>(ARG_SONG)

        song?.let {
            viewModel.setSong(it) // ViewModel correctly accepts Song
            viewModel.incrementPlayCount()

            // Directly populate TextViews and ImageView with initial song data
            view.findViewById<TextView>(R.id.songTitleID)?.text = it.title
            view.findViewById<TextView>(R.id.songArtistID)?.text = it.artists.joinToString(", ")

            val songImage = view.findViewById<ShapeableImageView>(R.id.songImageID)
            if (songImage != null) {
                if (it.image.isNotEmpty()) {
                    Glide.with(this)
                        .load(it.image)
                        .placeholder(R.drawable.unknown_song_img)
                        .error(R.drawable.unknown_song_img)
                        .into(songImage)
                } else {
                    songImage.setImageResource(R.drawable.unknown_song_img)
                }
            }

            // Populate Album, Release Date, and Credits
            view.findViewById<TextView>(R.id.songAlbumID)?.text = getString(R.string.album_format, it.album.ifEmpty { "N/A" })
            view.findViewById<TextView>(R.id.song_release_date_id)?.text = getString(R.string.release_date_format, it.releaseDate.ifEmpty { "N/A" })
            view.findViewById<TextView>(R.id.song_credits)?.text = getString(R.string.credits_format, it.credits.joinToString(", ").ifEmpty { "N/A" })

        } ?: run {
            Toast.makeText(requireContext(), "Song data not available.", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        view.findViewById<ImageButton>(R.id.btn_close)?.setOnClickListener {
            dismiss()
        }

        view.findViewById<View>(R.id.dialog_root)?.setOnClickListener {
            dismiss()
        }

        val root = view.findViewById<View>(R.id.dialog_root)
        val card = view.findViewById<View>(R.id.song_card_complex)

        root?.clearAnimation()
        card?.clearAnimation()

        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val slideIn = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_up)

        root?.startAnimation(fadeIn)
        card?.startAnimation(slideIn)

        val likeButton = view.findViewById<ImageButton>(R.id.btn_like)
        val starsRating = view.findViewById<MaterialRating>(R.id.stars_rating)
        val scoreTextView = view.findViewById<TextView>(R.id.val1)
        val rankingTextView = view.findViewById<TextView>(R.id.val2)
        val favoritesTextView = view.findViewById<TextView>(R.id.val3)

        viewModel.favoriteStatus.observe(viewLifecycleOwner) { isFavorite ->
            if (isFavorite) {
                likeButton.setImageResource(R.drawable.ic_favorite)
            } else {
                likeButton.setImageResource(R.drawable.ic_favorite_border)
            }
        }

        viewModel.userRating.observe(viewLifecycleOwner) { rating ->
            starsRating?.rating = rating
        }

        viewModel.globalStats.observe(viewLifecycleOwner) { result ->
            when (result) {
                is com.tunagold.oceantunes.utils.Result.Success -> {
                    result.data?.let { stats ->
                        scoreTextView?.text = String.format("%.1f", stats.avgScore)
                        rankingTextView?.text = stats.ranking.toString()
                        favoritesTextView?.text = stats.totalFavoriteCount.toString()
                    } ?: run {
                        scoreTextView?.text = "N/A"
                        rankingTextView?.text = "N/A"
                        favoritesTextView?.text = "N/A"
                    }
                }
                is com.tunagold.oceantunes.utils.Result.Error -> {
                    Toast.makeText(requireContext(), "Error loading stats: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    scoreTextView?.text = "Err"
                    rankingTextView?.text = "Err"
                    favoritesTextView?.text = "Err"
                }
                is com.tunagold.oceantunes.utils.Result.Loading -> {
                    scoreTextView?.text = "..."
                    rankingTextView?.text = "..."
                    favoritesTextView?.text = "..."
                }
            }
        }

        likeButton.setOnClickListener {
            viewModel.toggleFavoriteStatus()
        }

        starsRating?.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                viewModel.setUserRating(rating)
            }
        }
    }

    override fun dismiss() {
        if (isDismissing) return
        isDismissing = true

        val root = dialog?.findViewById<View>(R.id.dialog_root)
        val card = dialog?.findViewById<View>(R.id.song_card_complex)

        val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        val slideOut = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_down)

        var animationsEnded = 0
        val totalAnimations = 2

        fun checkDismiss() {
            animationsEnded++
            if (animationsEnded == totalAnimations) {
                super.dismiss()
            }
        }

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) = checkDismiss()
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        slideOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) = checkDismiss()
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        root?.startAnimation(fadeOut)
        card?.startAnimation(slideOut)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}