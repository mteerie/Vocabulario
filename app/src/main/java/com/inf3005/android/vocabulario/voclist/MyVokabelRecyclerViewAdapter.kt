package com.inf3005.android.vocabulario.voclist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inf3005.android.vocabulario.R
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
            .inflate(R.layout.fragment_vocabulary_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.deTextView.text = item.id
        holder.spTextView.text = item.content
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deTextView: TextView = view.findViewById(R.id.item_deutsch)
        val spTextView: TextView = view.findViewById(R.id.item_spanisch)

        override fun toString(): String {
            return super.toString() + " '" + spTextView.text + "'"
        }
    }
}