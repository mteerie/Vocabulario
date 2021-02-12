package com.inf3005.android.vocabulario.utilities

import android.app.Activity
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.data.Difficulty
import com.inf3005.android.vocabulario.data.Vocabulary

/**
 * Binding Adapter für die Listenelemente der RecyclerView. Erlauben Einsparungen am Code in
 * VocabularyAdapter, da dank Data Binding die Attribute dynamisch angepasst werden können.
 *
 * Beispiel in layout/fragment_list_item.xml: Text für entry_de wird über Attribut
 * app:deText aus dem Bindingadapter festgelegt.
 * */
@BindingAdapter("deText")
fun TextView.setDeText(entry: Vocabulary) {
    text = entry.de
}

@BindingAdapter("spText")
fun TextView.setSpText(entry: Vocabulary) {
    text = entry.sp
}

// Setze die Farbe der ImageView eines Listeneintrags entsprechend des Schwierigkeitsgrades.
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

// Zeige das TTS-Icon (Lautsprecher) nur für Einträge an, die nicht im Papierkorb sind.
@BindingAdapter("tts")
fun CoordinatorLayout.setVisibility(entry: Vocabulary) {
    visibility = when (entry.binned) {
        false -> VISIBLE
        true -> GONE
    }
}

// Wird für Listensortierung verwendet.
enum class SortBy { GERMAN, SPANISH, DIFFICULTY_ASC, DIFFICULTY_DESC }

/**
 * Adaptiert an Funktionalität von StackOverflow-Nutzer Gastón Saillén - Details in Dokumentation.
 *
 * Verstecke Tastatur bei Funktionsaufruf.
 * */
class KeyboardUtilities {
    companion object {
        fun hideKeyboard(activity: Activity) {
            val manager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

            // Elvis-Operator um Null-Safety zu gewährleisten.
            val view = activity.currentFocus ?: View(activity)

            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

/**
 * Adaptiert an Funktionalität von StackOverflow-Nutzer Mike M. - Details in Dokumentation.
 *
 * Interface wird von MainActivity erweitert um Funktion überschreiben zu können.
 * */
interface NavigationDrawerState {
    fun setDrawerState(enabled: Boolean)
}