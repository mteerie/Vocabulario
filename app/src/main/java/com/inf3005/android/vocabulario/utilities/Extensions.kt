package com.inf3005.android.vocabulario.utilities

import androidx.appcompat.widget.SearchView


/**
 * Extension-Funktion für SearchView-Objekte.
 * */
inline fun SearchView.onQueryChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        /**
         * Kann verwendet werden um zusätzliche Funktionalität beim Betätigen des 'Bestätigen'-
         * Buttons zu implementieren - hier nicht benötigt, deshalb wird lediglich false
         * zurückgegeben.
         * */
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        /**
         * Funktionsaufruf bei Änderung des Texts in der Sucheingabe.
         *
         * Dem an onQueryChanged übergebenen listener wird der neue Text mitgeteilt, damit er
         * auf weitere Änderungen reagieren kann.
         * */
        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}