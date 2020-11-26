package com.inf3005.android.vocabulario

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import timber.log.Timber

/**
 * Dieses Fragment dient als Startbildschirm der Anwendung und ist zugleich
 * der Startpunkt für die Vokabelabfrage.
 */
class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }


    /**
     * Zwecks Debugging implementierte Funktion
     */
    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("onDestroyView called.")
    }
}