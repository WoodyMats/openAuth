package com.woodymats.openauth.ui.courseContentsList

import android.view.View
import com.woodymats.openauth.models.local.ContentEntity

interface ContentsRecyclerViewClickListener {

    fun onContentItemClicked(view: View, content: ContentEntity)
}