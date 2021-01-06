package com.inf3005.android.vocabulario.utilities

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.inf3005.android.vocabulario.data.Vocabulary

@BindingAdapter("deText")
fun TextView.setDeText(entry: Vocabulary) {
    text = entry.de
}

@BindingAdapter("spText")
fun TextView.setSpText(entry: Vocabulary) {
    text = entry.sp
}