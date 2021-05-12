package com.woodymats.openauth.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woodymats.openauth.R
import com.woodymats.openauth.databinding.ChapterCardItemAdapterBinding
import com.woodymats.openauth.models.local.ChapterEntity

class ChaptersAdapter :
    ListAdapter<ChapterEntity, ChaptersAdapter.ChapterViewHolder>(ChaptersDiffCallback()) {

    inner class ChapterViewHolder(val adapterChapterItemAdapterBinding: ChapterCardItemAdapterBinding) :
        RecyclerView.ViewHolder(adapterChapterItemAdapterBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChapterViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.chapter_card_item_adapter,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.adapterChapterItemAdapterBinding.chapter = getItem(position)
        // holder.adapterChapterItemAdapterBinding.chapterCardView.setOnClickListener {
        //     if (holder.adapterChapterItemAdapterBinding.chapterDescription.height != WRAP_CONTENT) {
        //         TransitionManager.beginDelayedTransition(
        //             holder.adapterChapterItemAdapterBinding.chapterCardView,
        //             AutoTransition()
        //         )
        //         holder.adapterChapterItemAdapterBinding.chapterDescription.height = WRAP_CONTENT
        //     } else {
        //         TransitionManager.beginDelayedTransition(
        //             holder.adapterChapterItemAdapterBinding.chapterCardView,
        //             AutoTransition()
        //         )
        //         holder.adapterChapterItemAdapterBinding.chapterDescription.height = MATCH_CONSTRAINT
        //     }
        // }
        holder.adapterChapterItemAdapterBinding.executePendingBindings()
    }
}

class ChaptersDiffCallback : DiffUtil.ItemCallback<ChapterEntity>() {
    override fun areItemsTheSame(oldItem: ChapterEntity, newItem: ChapterEntity): Boolean {
        return oldItem.chapterId == newItem.chapterId
    }

    override fun areContentsTheSame(oldItem: ChapterEntity, newItem: ChapterEntity): Boolean {
        return oldItem == newItem
    }
}