package com.inf3005.android.vocabulario.utilities

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.BindingAdapter
import com.inf3005.android.vocabulario.database.Vocabulary

@BindingAdapter("deText")
fun TextView.setDeText(entry: Vocabulary) {
    text = entry.de
}

@BindingAdapter("spText")
fun TextView.setSpText(entry: Vocabulary) {
    text = entry.sp
}