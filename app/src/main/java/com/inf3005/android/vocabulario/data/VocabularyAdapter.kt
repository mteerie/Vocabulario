package com.inf3005.android.vocabulario.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inf3005.android.vocabulario.databinding.FragmentListItemBinding

/**
 * Adapter für die RecyclerView in ListFragment und BinFragment. Regelt die Darstellung der
 * Daten aus der Datenbank innerhalb der Liste.
 *
 * Arbeitet mit ListAdapter und DiffUtil um effizient auf Änderungen an Listeneinträgen zu
 * reagieren.
 *
 * Data Binding wird verwendet.
 * */
class VocabularyAdapter(private val clickListener: EntryClickListener) :
    ListAdapter<Vocabulary, VocabularyAdapter.ViewHolder>(VocabularyDifferences()) {

    interface EntryClickListener {
        fun onCardClick(entry: Vocabulary)
        fun onTextToSpeechIconClick(entry: Vocabulary)
    }

    inner class ViewHolder(private val binding: FragmentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION)
                        clickListener.onCardClick(getItem(adapterPosition))
                }

                textToSpeechIconHolder.setOnClickListener {
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

    fun getEntryAt(position: Int): Vocabulary = getItem(position)

    class VocabularyDifferences : DiffUtil.ItemCallback<Vocabulary>() {
        override fun areItemsTheSame(oldItem: Vocabulary, newItem: Vocabulary) =
            oldItem.vocId == newItem.vocId

        override fun areContentsTheSame(oldItem: Vocabulary, newItem: Vocabulary) =
            oldItem == newItem
    }
}