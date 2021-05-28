package com.woodymats.openauth.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woodymats.openauth.databinding.CourseContentItemAdapterBinding
import com.woodymats.openauth.models.local.ContentEntity
import com.woodymats.openauth.ui.courseContentsList.ContentsRecyclerViewClickListener

class CourseContentsListAdapter(private val listener: ContentsRecyclerViewClickListener) :
    ListAdapter<ContentEntity, CourseContentsListAdapter.ViewHolder>(ContentsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            CourseContentItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.content = getItem(position)
        holder.binding.itemLayout.setOnClickListener { listener.onContentItemClicked(it, getItem(position)) }
        holder.binding.executePendingBindings()
    }

    inner class ViewHolder(val binding: CourseContentItemAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ContentsDiffCallback : DiffUtil.ItemCallback<ContentEntity>() {
        override fun areItemsTheSame(oldItem: ContentEntity, newItem: ContentEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ContentEntity, newItem: ContentEntity): Boolean {
            return oldItem == newItem
        }
    }
}