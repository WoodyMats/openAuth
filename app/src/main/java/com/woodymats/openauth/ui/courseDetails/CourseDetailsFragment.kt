package com.woodymats.openauth.ui.courseDetails

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.woodymats.openauth.R
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.databinding.FragmentCourseDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseDetailsFragment : Fragment() {

    private lateinit var courseDetailsViewModel: CourseDetailsViewModel
    private lateinit var binding: FragmentCourseDetailsBinding
    private val args: CourseDetailsFragmentArgs by navArgs()
    private val emailId: Long by lazy(LazyThreadSafetyMode.NONE) { args.emailId }
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            // Scope the transition to a view in the hierarchy so we know it will be added under
            // the bottom app bar but over the elevation scale of the exiting HomeFragment.
            drawingViewId = R.id.nav_host_fragment
            duration = 400L
            scrimColor = Color.TRANSPARENT
            // setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCourseDetailsBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiScope.launch(Dispatchers.IO){
            val course = getInstance(requireContext()).courseDAO.getCourseById(emailId)
            withContext(Dispatchers.Main){
                binding.textGallery.text = course.title
            }

        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}