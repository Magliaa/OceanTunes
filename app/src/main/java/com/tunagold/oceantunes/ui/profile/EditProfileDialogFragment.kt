package com.tunagold.oceantunes.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.tunagold.oceantunes.R
import com.google.android.material.button.MaterialButton

class EditProfileDialogFragment(
    private val currentName: String,
    private val onSave: (String, Uri?) -> Unit
) : DialogFragment() {

    private lateinit var nameEditText: EditText
    private lateinit var profileImageView: ImageView
    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 101
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_edit_profile, container, false)

        nameEditText = view.findViewById(R.id.editName)
        profileImageView = view.findViewById(R.id.editProfileImage)
        val saveButton = view.findViewById<MaterialButton>(R.id.saveButton)
        val cancelButton = view.findViewById<MaterialButton>(R.id.cancelButton)

        nameEditText.setText(currentName)

        profileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString().trim()
            if (newName.isNotEmpty()) {
                onSave(newName, selectedImageUri)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Inserisci un nome valido", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            profileImageView.setImageURI(selectedImageUri)
        }
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
