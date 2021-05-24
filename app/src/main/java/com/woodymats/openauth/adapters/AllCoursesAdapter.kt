package com.woodymats.openauth.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woodymats.openauth.R
import com.woodymats.openauth.databinding.CourseCardItemAdapterBinding
import com.woodymats.openauth.models.local.CourseEntity
import com.woodymats.openauth.ui.home.CourseRecyclerViewClickListener

class AllCoursesAdapter(private val listener: CourseRecyclerViewClickListener): ListAdapter<CourseEntity, AllCoursesAdapter.AllCoursesViewHolder>(AllCoursesDiffCallback()) {

    // override fun getItemCount() = enrollments.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AllCoursesViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.course_card_item_adapter,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: AllCoursesViewHolder, position: Int) {
        holder.adapterCourseCardItemAdapterBinding.course = getItem(position)
        holder.adapterCourseCardItemAdapterBinding.root.setOnClickListener { listener.onCourseItemClicked(it, getItem(position)) }
        holder.adapterCourseCardItemAdapterBinding.executePendingBindings()
    }

    inner class AllCoursesViewHolder(val adapterCourseCardItemAdapterBinding: CourseCardItemAdapterBinding) :
        RecyclerView.ViewHolder(adapterCourseCardItemAdapterBinding.root)
}

class AllCoursesDiffCallback : DiffUtil.ItemCallback<CourseEntity>() {
    override fun areItemsTheSame(oldItem: CourseEntity, newItem: CourseEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CourseEntity, newItem: CourseEntity): Boolean {
        return oldItem == newItem
    }
}