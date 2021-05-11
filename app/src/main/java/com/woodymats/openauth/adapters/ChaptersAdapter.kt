package com.woodymats.openauth.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woodymats.openauth.R
import com.woodymats.openauth.databinding.ChapterCardItemAdapterBinding
import com.woodymats.openauth.models.Chapter

class ChaptersAdapter: ListAdapter<Chapter, ChaptersAdapter.ChapterViewHolder>(ChaptersDiffCallback()) {

    inner class ChapterViewHolder(val adapterChapterItemAdapterBinding: ChapterCardItemAdapterBinding): RecyclerView.ViewHolder(adapterChapterItemAdapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= ChapterViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.chapter_card_item_adapter,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.adapterChapterItemAdapterBinding.chapter = getItem(position)
        holder.adapterChapterItemAdapterBinding.executePendingBindings()
    }
}

class ChaptersDiffCallback: DiffUtil.ItemCallback<Chapter>() {
    override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem.chapterId == newItem.chapterId
    }

    override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem == newItem
    }
}