package com.inf3005.android.vocabulario.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inf3005.android.vocabulario.R
import com.inf3005.android.vocabulario.database.Vocabulary
import com.inf3005.android.vocabulario.databinding.FragmentListItemBinding

class VocabularyAdapter(private val listener: EntryClickListener) : ListAdapter<Vocabulary, VocabularyAdapter.ViewHolder>(VocabularyDifferences())  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, listener)
    }

    class ViewHolder(private val binding: FragmentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: Vocabulary, listener: EntryClickListener) {
            binding.vocabulary = entry
            binding.clickListener = listener
            binding.executePendingBindings()
        }


        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val binding = FragmentListItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ViewHolder(binding)
            }
        }
    }

    fun getEntryAt(position: Int) : Vocabulary{
        return getItem(position)
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

class EntryClickListener(val listener: (id: Long) -> Unit) {
    fun onClick(entry: Vocabulary) = listener(entry.id)
}