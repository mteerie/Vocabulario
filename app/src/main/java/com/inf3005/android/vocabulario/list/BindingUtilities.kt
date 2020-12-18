package com.inf3005.android.vocabulario.list

import android.widget.TextView
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