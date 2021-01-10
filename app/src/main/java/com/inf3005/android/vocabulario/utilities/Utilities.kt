package com.inf3005.android.vocabulario.utilities

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.data.Difficulty
import com.inf3005.android.vocabulario.data.Vocabulary
import dagger.hilt.android.qualifiers.ApplicationContext

@BindingAdapter("deText")
fun TextView.setDeText(entry: Vocabulary) {
    text = entry.de
}

@BindingAdapter("spText")
fun TextView.setSpText(entry: Vocabulary) {
    text = entry.sp
}

@BindingAdapter("difficulty")
fun ImageView.setDifficultyColor(entry: Vocabulary) {
    setColorFilter(
        when (entry.difficulty) {
            Difficulty.INTERMEDIATE -> ContextCompat.getColor(context, R.color.yellow_600)
            Difficulty.HARD -> ContextCompat.getColor(context, R.color.red_600)
            else -> ContextCompat.getColor(context, R.color.green_600)
        }
    )
}

/**
 * Adaptiert an Funktionalität von stackoverflow-Nutzer Gastón Saillén (63036385).
 *
 * Die Funktion wird innerhalb eines companion objects definiert, um sie in Activities und
 * Fragments direkt über den Klassennamen aufrufen zu können.
 *
 * hideKeyboard wird verwendet, um die Android-on-Screen-Tastatur zu verstecken, sowohl in der
 * MainActivity - beim Betätigen des Android Back Buttons, Ausführen der Funktion
 * onSupportNavigateUp - und bei Beenden des AddEditFragments durch den onClickListener des
 * submitButtons.
 * */
class KeyboardUtilities {
    companion object {
        fun hideKeyboard(activity: Activity) {
            val manager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

            /**
             * Für Null Safety wird der Elvis-Operator ?: verwendet, um notfalls den currentFocus
             * der activity des jeweiligen Kontexts bei Aufruf im val view speichern zu können.
             * */
            val view = activity.currentFocus ?: View(activity)

            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

/**
 * Adaptiert an Funktionalität von stackoverflow-Nutzer Mike M. (36250280).
 *
 * Es wird ein Interface implementiert, über dessen Funktion setDrawerState festgelegt wird,
 * ob der Navigation Drawer per Gesten geöffnet werden kann.
 * */
interface NavigationDrawerState {
    fun setDrawerState(enabled: Boolean)
}