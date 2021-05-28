package com.woodymats.openauth.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import com.woodymats.openauth.R
import com.woodymats.openauth.databinding.SearchCourseItemBinding
import com.woodymats.openauth.models.local.CourseEntity

class SearchCoursesAdapter(private var coursesList: List<CourseEntity>) : BaseAdapter(), Filterable {
    private var listFilter: ListFilter? = null
    private val allCoursesList = coursesList

    inner class SearchCoursesAutoCompleteHolder(binding: SearchCourseItemBinding) {
        val view = binding
    }

    override fun getCount(): Int = coursesList.size

    override fun getItem(position: Int): CourseEntity {
        return coursesList[position]
    }

    override fun getItemId(position: Int): Long {
        return coursesList[position].id
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder = SearchCoursesAutoCompleteHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.search_course_item,
                parent,
                false
            )
        )

        viewHolder.view.course = getItem(position)

        return viewHolder.view.root

    }

    override fun getFilter(): Filter {
        if (listFilter == null) {
            listFilter = ListFilter()
        }
        return listFilter as ListFilter
    }

    inner class ListFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var constraintTemp = constraint
            val results = FilterResults()

            if (constraint != null && constraint.isNotEmpty()) {
                constraintTemp = constraint.toString().toUpperCase()

                val filteredList: MutableList<CourseEntity> = mutableListOf()

                for (course in allCoursesList) {
                    if (course.title.contains(constraint, true)) {
                        filteredList.add(course)
                    }
                }
                results.count = filteredList.size
                results.values = filteredList
            } else {
                results.count = allCoursesList.size
                results.values = allCoursesList
            }
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            coursesList = results?.values as List<CourseEntity>
            notifyDataSetChanged()
        }
    }

}