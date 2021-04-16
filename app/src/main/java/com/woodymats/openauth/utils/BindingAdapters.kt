package com.woodymats.openauth.utils

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.woodymats.openauth.adapters.MyCoursesAdapter
import com.woodymats.openauth.R
import com.woodymats.openauth.adapters.AllCoursesAdapter
import com.woodymats.openauth.models.Course
import com.woodymats.openauth.models.Enrollment

@BindingAdapter("loadingInProcess")
fun showLoading(view: View, loadingInProcess: Boolean) {
    view.visibility = if (loadingInProcess) View.VISIBLE else View.GONE
}

@BindingAdapter("customFocusChangeListener")
fun customFocusChangeListener(view: View, focusChangeListener: View.OnFocusChangeListener) {
    view.onFocusChangeListener = focusChangeListener
}

@BindingAdapter("enrollmentData")
fun bindEnrollmentsRecyclerView(recyclerView: RecyclerView, data: List<Enrollment>?) {
    val adapter = recyclerView.adapter as MyCoursesAdapter
    adapter.submitList(data)
}

@BindingAdapter("coursesData")
fun bindAllCoursesRecyclerView(recyclerView: RecyclerView, data: List<Course>?) {
    val adapter = recyclerView.adapter as AllCoursesAdapter
    adapter.submitList(data)
}

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

@BindingAdapter("showOnlyWhenEmpty")
fun View.showOnlyWhenIsNotEmpty(data: List<Any>?) {
    visibility = when {
        data == null || data.isEmpty() -> View.GONE
        else -> View.VISIBLE
    }
}