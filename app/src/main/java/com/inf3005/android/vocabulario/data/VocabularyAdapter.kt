package com.inf3005.android.vocabulario.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inf3005.android.vocabulario.databinding.FragmentListItemBinding

class VocabularyAdapter(private val clickListener: EntryClickListener) :
    ListAdapter<Vocabulary, VocabularyAdapter.ViewHolder>(VocabularyDifferences()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FragmentListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: FragmentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION)
                        clickListener.onCardClick(getItem(adapterPosition))
                }

                textToSpeechIcon.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION)
                        clickListener.onTextToSpeechIconClick(getItem(adapterPosition))
                }
            }
        }

        fun bind(entry: Vocabulary) {
            binding.apply {
                binding.vocabulary = entry
                binding.executePendingBindings()
            }
        }
    }

    interface EntryClickListener {
        fun onCardClick(entry: Vocabulary)
        fun onTextToSpeechIconClick(entry: Vocabulary)
    }

    fun getEntryAt(position: Int): Vocabulary {
        return getItem(position)
    }

    class VocabularyDifferences : DiffUtil.ItemCallback<Vocabulary>() {
        override fun areItemsTheSame(oldItem: Vocabulary, newItem: Vocabulary) =
            oldItem.vocId == newItem.vocId

        override fun areContentsTheSame(oldItem: Vocabulary, newItem: Vocabulary) =
            oldItem == newItem
    }
}