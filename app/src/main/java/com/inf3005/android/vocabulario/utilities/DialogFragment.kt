package com.inf3005.android.vocabulario.utilities

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class InputEntryDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Test")
                .setPositiveButton("OK") { _, _ ->
                    Toast.makeText(this.activity, "fuck off", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Nein") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}