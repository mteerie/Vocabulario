package com.inf3005.android.vocabulario.voclist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.database.Vocabulary

class VocabularyAdapter : ListAdapter<Vocabulary, VocabularyAdapter.ViewHolder>(VocabularyDifferences())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item.de, item.sp)
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        private val deTextView: TextView = view.findViewById(R.id.item_deutsch)
        private val spTextView: TextView = view.findViewById(R.id.item_spanisch)

        fun bind(deText: String?, spText: String?) {
            deTextView.text = deText
            spTextView.text = spText
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_vocabulary_list_item, parent, false)
                return ViewHolder(view)
            }
        }
    }
}

class VocabularyDifferences : DiffUtil.ItemCallback<Vocabulary>() {
    override fun areItemsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
        return oldItem == newItem
    }
}