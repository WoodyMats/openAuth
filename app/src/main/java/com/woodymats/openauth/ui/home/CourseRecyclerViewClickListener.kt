package com.woodymats.openauth.ui.home

import android.view.View
import com.woodymats.openauth.models.CourseEntity

interface CourseRecyclerViewClickListener {

    fun onCourseItemClicked(view: View, course: CourseEntity)
}