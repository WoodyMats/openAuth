package com.woodymats.openauth.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import com.google.android.material.transition.MaterialFadeThrough
import com.woodymats.openauth.adapters.AllCoursesAdapter
import com.woodymats.openauth.adapters.MyCoursesAdapter
import com.woodymats.openauth.databinding.FragmentHomeBinding
import com.woodymats.openauth.models.local.CourseEntity
import com.woodymats.openauth.utils.WORKER_STATUS
import com.woodymats.openauth.utils.hideKeyboard

class HomeFragment : Fragment(), CourseRecyclerViewClickListener {

    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var binding: FragmentHomeBinding
    private var fragmentContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = 200L
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setUpViewModel()
        setUpRecyclerViews()
        setUpAutoCompleteTextView()
        setUpObservers()
        return binding.root
    }

    private fun setUpObservers() {
        viewModel.outputWorkInfo.observe(
            viewLifecycleOwner,
            { listOfWorkInfo ->
                if (!listOfWorkInfo.isNullOrEmpty()) {
                    val workInfo = listOfWorkInfo[0]
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val succeeded = workInfo.outputData.getBoolean(WORKER_STATUS, false)
                            if (succeeded) {
                                viewModel.getCoursesAndEnrollmentsFromCache()
                            }
                        }
                        WorkInfo.State.ENQUEUED -> {
                            viewModel.getCoursesAndEnrollmentsFromCache()
                        }
                        WorkInfo.State.CANCELLED -> {
                        } // Toast.makeText(context, "Work has cancelled!", Toast.LENGTH_SHORT).show()
                        WorkInfo.State.FAILED -> {
                            val succeeded = workInfo.outputData.getBoolean(WORKER_STATUS, false)
                            if (!succeeded) {
                                viewModel.getCoursesAndEnrollmentsFromCache()
                            }
                        }
                        WorkInfo.State.BLOCKED -> {
                        } // Toast.makeText(context, "Work has blocked!", Toast.LENGTH_SHORT).show()
                        WorkInfo.State.RUNNING -> {
                        } // Toast.makeText(context, "Work is running!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun setUpAutoCompleteTextView() {
        binding.coursesSearchBar.setOnItemClickListener { parent, view, position, id ->
            binding.coursesSearchBar.text.clear()
            requireActivity().hideKeyboard(binding.coursesSearchBar)
            val action = HomeFragmentDirections.actionNavHomeToCourseDetailsFragment(id)
            findNavController().navigate(action)
        }
    }

    private fun setUpRecyclerViews() {
        binding.myCoursesRecycler.also {
            it.layoutManager =
                LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = MyCoursesAdapter(this)
        }
        binding.allCoursesRecycler.also {
            it.layoutManager =
                LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = AllCoursesAdapter(this)
        }
    }

    private fun setUpViewModel() {
        binding.lifecycleOwner = this
        val application = requireActivity().application
        val viewModelFactory = HomeFragmentViewModelFactory(application)
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(HomeFragmentViewModel::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeFragmentViewModel::class.java)
        // binding.executePendingBindings()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onCourseItemClicked(view: View, course: CourseEntity, hideBottomBar: Boolean) {
        // exitTransition = MaterialElevationScale(false).apply {
        //     duration = 300L
        // }
        // reenterTransition = MaterialElevationScale(true).apply {
        //     duration = 300L
        // }
        // val courseCardDetailTransitionName = getString(R.string.course_details_transition_name)
        // val extras = FragmentNavigatorExtras(view to courseCardDetailTransitionName)
        val action =
            HomeFragmentDirections.actionNavHomeToCourseDetailsFragment(course.id, hideBottomBar)
        findNavController().navigate(action) // , extras)
    }
}
