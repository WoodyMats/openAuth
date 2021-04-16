package com.woodymats.openauth.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woodymats.openauth.R
import com.woodymats.openauth.databinding.CourseCardItemAdapterBinding
import com.woodymats.openauth.models.Enrollment

class MyCoursesAdapter: //(private val enrollments: List<Enrollment>) :
    ListAdapter<Enrollment, MyCoursesAdapter.MyCourseViewHolder>(EnrollmentsDiffCallback()) {

    // override fun getItemCount() = enrollments.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyCourseViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.course_card_item_adapter,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: MyCourseViewHolder, position: Int) {
        holder.adapterCourseCardItemAdapterBinding.course = getItem(position).course
        holder.adapterCourseCardItemAdapterBinding.executePendingBindings()
    }

    inner class MyCourseViewHolder(val adapterCourseCardItemAdapterBinding: CourseCardItemAdapterBinding) :
        RecyclerView.ViewHolder(adapterCourseCardItemAdapterBinding.root)
}

class EnrollmentsDiffCallback : DiffUtil.ItemCallback<Enrollment>() {
    override fun areItemsTheSame(oldItem: Enrollment, newItem: Enrollment): Boolean {
        return oldItem.course.id == newItem.course.id
    }

    override fun areContentsTheSame(oldItem: Enrollment, newItem: Enrollment): Boolean {
        return oldItem == newItem
    }
}