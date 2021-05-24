package com.woodymats.openauth.utils

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.woodymats.openauth.adapters.MyCoursesAdapter
import com.woodymats.openauth.R
import com.woodymats.openauth.adapters.AllCoursesAdapter
import com.woodymats.openauth.adapters.ChaptersAdapter
import com.woodymats.openauth.adapters.CourseContentsListAdapter
import com.woodymats.openauth.adapters.SearchCoursesAdapter
import com.woodymats.openauth.models.local.ChapterEntity
import com.woodymats.openauth.models.Enrollment
import com.woodymats.openauth.models.local.ContentEntity
import com.woodymats.openauth.models.local.CourseEntity

@BindingAdapter("loadingInProcess")
fun showLoading(view: View, loadingInProcess: Boolean) {
    view.visibility = if (loadingInProcess) View.VISIBLE else View.GONE
}

@BindingAdapter("customFocusChangeListener")
fun customFocusChangeListener(view: View, focusChangeListener: View.OnFocusChangeListener) {
    view.onFocusChangeListener = focusChangeListener
}

@BindingAdapter("listData")
fun bindEnrollmentsRecyclerView(recyclerView: RecyclerView, data: List<Enrollment>?) {
    val adapter = recyclerView.adapter as MyCoursesAdapter
    adapter.submitList(data)
}

@BindingAdapter("listData")
fun bindSearchCourseAutoCompleteTextView(autoCompleteTextView: MaterialAutoCompleteTextView, data: List<CourseEntity>?) {
    if (data != null) {
        autoCompleteTextView.setAdapter(SearchCoursesAdapter(data))
    }
}

@BindingAdapter("listData")
fun bindAllCoursesRecyclerView(recyclerView: RecyclerView, data: List<CourseEntity>?) {
    val adapter = recyclerView.adapter as AllCoursesAdapter
    adapter.submitList(data)
}

@BindingAdapter("listData")
fun bindChaptersRecyclerView(recyclerView: RecyclerView, data: List<ChapterEntity>?) {
    val adapter = recyclerView.adapter as ChaptersAdapter
    val sortedList = data?.sortedBy { it.order }
    adapter.submitList(sortedList)
}

@BindingAdapter("listData")
fun bindContentsRecyclerView(recyclerView: RecyclerView, data: List<ContentEntity>?) {
    val adapter = recyclerView.adapter as CourseContentsListAdapter
    val sortedList = data?.sortedBy { it.order }
    adapter.submitList(sortedList)
}

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri() //.buildUpon().scheme("http").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

@BindingAdapter("isContentCompleted")
fun changeTintColorInContentCompleted(imageView: ImageView, completed: Int) {
    if (completed == 1) {
        imageView.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
    }
}

@BindingAdapter("showOnlyWhenEmpty")
fun View.showOnlyWhenIsNotEmpty(data: List<Any>?) {
    visibility = when {
        data == null || data.isEmpty() -> View.GONE
        else -> View.VISIBLE
    }
}