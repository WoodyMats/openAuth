package com.woodymats.openauth.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woodymats.openauth.R
import com.woodymats.openauth.databinding.CourseCardItemAdapterBinding
import com.woodymats.openauth.models.Course
import com.woodymats.openauth.ui.home.CourseRecyclerViewClickListener

class AllCoursesAdapter(private val listener: CourseRecyclerViewClickListener): ListAdapter<Course, AllCoursesAdapter.AllCoursesViewHolder>(AllCoursesDiffCallback()) {

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
        holder.adapterCourseCardItemAdapterBinding.course = getItem(position).course
        holder.adapterCourseCardItemAdapterBinding.root.setOnClickListener { listener.onCourseItemClicked(it, getItem(position).course) }
        holder.adapterCourseCardItemAdapterBinding.executePendingBindings()
    }

    inner class AllCoursesViewHolder(val adapterCourseCardItemAdapterBinding: CourseCardItemAdapterBinding) :
        RecyclerView.ViewHolder(adapterCourseCardItemAdapterBinding.root)
}

class AllCoursesDiffCallback : DiffUtil.ItemCallback<Course>() {
    override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
        return oldItem.course.id == newItem.course.id
    }

    override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
        return oldItem == newItem
    }
}