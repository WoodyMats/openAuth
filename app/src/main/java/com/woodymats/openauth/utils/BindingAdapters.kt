package com.woodymats.openauth.utils

import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter

@BindingAdapter("loadingInProcess")
fun showLoading(view: View, loadingInProcess: Boolean) {
    view.visibility = if (loadingInProcess) View.VISIBLE else View.GONE
}