package com.phamthang.reverseaudio.extention

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

fun EditText.onFocusChange(onFocus: (hasFocus: Boolean) -> Unit) {
    this.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        onFocus(hasFocus)
    }
}

fun EditText.onTextChange(onTextChange: (text: String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChange(s?.toString().orEmpty())
        }
        override fun afterTextChanged(s: Editable?) {}
    })
}

