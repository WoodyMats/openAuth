package com.woodymats.openauth.utils

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("loadingInProcess")
fun showLoading(view: View, loadingInProcess: Boolean) {
    view.visibility = if (loadingInProcess) View.VISIBLE else View.GONE
}

@BindingAdapter("customFocusChangeListener")
fun customFocusChangeListener(view: View, focusChangeListener: View.OnFocusChangeListener) {
    view.onFocusChangeListener = focusChangeListener
}