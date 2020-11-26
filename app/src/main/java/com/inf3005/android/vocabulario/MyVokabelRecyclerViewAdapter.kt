package com.inf3005.android.vocabulario

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.inf3005.android.vocabulario.dummy.DummyContent.DummyItem

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyVokabelRecyclerViewAdapter(
    private val values: List<DummyItem>
) : RecyclerView.Adapter<MyVokabelRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_vokabelliste_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content

        /**
        * Prüft die Position der Listenelemente und setzt die Hintergrundfarbe eines Elements bei gerader Position Hellgrau, andernfalls Weiß
        */
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.WHITE)
        }
        else {
            holder.itemView.setBackgroundColor(Color.LTGRAY)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.item_deutsch)
        val contentView: TextView = view.findViewById(R.id.item_spanisch)

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}