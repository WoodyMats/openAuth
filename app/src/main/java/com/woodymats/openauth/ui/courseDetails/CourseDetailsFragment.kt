package com.woodymats.openauth.ui.courseDetails

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.woodymats.openauth.R
import com.woodymats.openauth.adapters.ChaptersAdapter
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.databinding.FragmentCourseDetailsBinding
import com.woodymats.openauth.models.local.ChapterEntity
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.PREFERENCES

class CourseDetailsFragment : Fragment(), ChaptersRecyclerViewClickListener {

    private lateinit var viewModel: CourseDetailsViewModel
    private lateinit var binding: FragmentCourseDetailsBinding
    private var fragmentContext: Context? = null
    private val args: CourseDetailsFragmentArgs by navArgs()
    private val courseId: Long by lazy(LazyThreadSafetyMode.NONE) { args.courseId }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            // Scope the transition to a view in the hierarchy so we know it will be added under
            // the bottom app bar but over the elevation scale of the exiting HomeFragment.
            drawingViewId = R.id.nav_host_fragment
            duration = 200L
            scrimColor = Color.TRANSPARENT
            // setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCourseDetailsBinding.inflate(layoutInflater)
        setUpViewModel()
        setUpRecyclerView()
        setUpObservers()
        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.chaptersRecyclerView.also {
            it.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
            it.adapter = ChaptersAdapter(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onDetach() {
        fragmentContext = null
        super.onDetach()
    }

    private fun setUpViewModel() {
        binding.lifecycleOwner = this
        val viewModelFactory = CourseDetailsViewModelFactory(fragmentContext!!.getSharedPreferences(PREFERENCES, MODE_PRIVATE), getInstance(fragmentContext!!), courseId)
        binding.viewModel = ViewModelProvider(this, viewModelFactory).get(CourseDetailsViewModel::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CourseDetailsViewModel::class.java)
    }

    private fun setUpObservers() {
        viewModel.callStatus.observe(this.viewLifecycleOwner, {
            when (it) {
                ApiCallStatus.UNKNOWNERROR -> Snackbar.make(
                    binding.root,
                    R.string.unknown_error,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.NOINTERNETERROR -> Snackbar.make(
                    binding.root,
                    R.string.no_internet_connection,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.AUTHERROR -> Snackbar.make(
                    binding.root,
                    R.string.wrong_credentials,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.SERVERERROR -> Snackbar.make(
                    binding.root,
                    R.string.already_enrolled,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.SUCCESS -> {
                    viewModel.hideEnrollLinear()
                    Snackbar.make(
                        binding.root,
                        getString(R.string.enrolled_to_course, viewModel.course.value!!.title),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun setUpListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onChapterItemClicked(view: View, chapter: ChapterEntity) {
        val action = CourseDetailsFragmentDirections.actionCourseDetailsFragmentToCourseContentsListFragment(chapter.title, chapter.chapterId)
        findNavController().navigate(action)
    }
}