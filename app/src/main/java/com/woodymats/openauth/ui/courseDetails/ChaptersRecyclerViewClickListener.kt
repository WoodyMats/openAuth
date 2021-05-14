package com.woodymats.openauth.ui.courseDetails

import android.view.View
import com.woodymats.openauth.models.local.ChapterEntity

interface ChaptersRecyclerViewClickListener {

    fun onChapterItemClicked(view: View, chapter: ChapterEntity)
}