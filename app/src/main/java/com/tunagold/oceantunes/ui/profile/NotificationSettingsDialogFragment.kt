package com.tunagold.oceantunes.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.tunagold.oceantunes.R

class NotificationSettingsDialogFragment(
    private val trendingEnabled: Boolean,
    private val topSongEnabled: Boolean,
    private val onSave: (Boolean, Boolean) -> Unit
) : DialogFragment() {

    private lateinit var trendingSwitch: Switch
    private lateinit var topSongSwitch: Switch

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_notification_profile, container, false)

        trendingSwitch = view.findViewById(R.id.switchTrending)
        topSongSwitch = view.findViewById(R.id.switchTopSong)
        val saveButton = view.findViewById<MaterialButton>(R.id.saveButton)
        val cancelButton = view.findViewById<MaterialButton>(R.id.cancelButton)

        trendingSwitch.isChecked = trendingEnabled
        topSongSwitch.isChecked = topSongEnabled

        saveButton.setOnClickListener {
            onSave(trendingSwitch.isChecked, topSongSwitch.isChecked)
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
