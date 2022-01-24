package com.example.map.googlemap.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlinx.android.synthetic.main.search_place_dialog.view.*

fun EditText.showKeyboard() {
        post {
            requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et_keyword, InputMethodManager.SHOW_FORCED)
        }
}