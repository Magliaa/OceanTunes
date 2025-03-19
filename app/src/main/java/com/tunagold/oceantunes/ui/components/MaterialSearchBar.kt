package com.tunagold.oceantunes.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tunagold.oceantunes.R

class MaterialSearchBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val textInputLayout: TextInputLayout
    private val editText: TextInputEditText

    var onSearchAction: ((String) -> Unit)? = null
    var onClearAction: (() -> Unit)? = null

    init {
        // Inflate the custom layout
        val rootView = LayoutInflater.from(context).inflate(R.layout.searchbar_layout, this, true)

        // Initialize views
        textInputLayout = rootView.findViewById(R.id.textInputLayout)
        editText = rootView.findViewById(R.id.editText)

        // Set up listeners or other logic here
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = editText.text?.toString() ?: ""
                onSearchAction?.invoke(query)
                true
            } else {
                false
            }
        }
    }

    // Expose the query text
    fun getQuery(): String {
        return editText.text?.toString() ?: ""
    }

    // Clear the search text
    fun clear() {
        editText.text?.clear()
        onClearAction?.invoke()
    }
}