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
import com.tunagold.oceantunes.R
import com.tunagold.oceantunes.ui.components.SongSummary

class SongCardDialogFragment : DialogFragment() {

    private var isDismissing = false

    companion object {
        fun newInstance(song: Triple<String, String, Int>): SongCardDialogFragment {
            val fragment = SongCardDialogFragment()
            val args = Bundle().apply {
                putString("title", song.first)
                putString("artist", song.second)
                putInt("img", song.third)
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

        val title = arguments?.getString("title") ?: ""
        val artist = arguments?.getString("artist") ?: ""
        val img = arguments?.getInt("img") ?: R.drawable.unknown_song_img

        val summary = view.findViewById<SongSummary>(R.id.song_summary)
        summary.setTitle(title)
        summary.setArtist(artist)
        summary.setImage(img)

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
        var isLiked = false

        likeButton.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                likeButton.setImageResource(R.drawable.ic_favorite) //
                Toast.makeText(requireContext(), "Aggiunta ai preferiti", Toast.LENGTH_SHORT).show()
            } else {
                likeButton.setImageResource(R.drawable.ic_favorite_border)
                Toast.makeText(requireContext(), "Rimossa dai preferiti", Toast.LENGTH_SHORT).show()
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
}
