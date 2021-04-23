package com.woodymats.openauth.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.woodymats.openauth.R
import com.woodymats.openauth.adapters.AllCoursesAdapter
import com.woodymats.openauth.adapters.MyCoursesAdapter
import com.woodymats.openauth.databinding.FragmentHomeBinding
import com.woodymats.openauth.models.CourseEntity

class HomeFragment : Fragment(), CourseRecyclerViewClickListener {

    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var binding: FragmentHomeBinding
    private var fragmentContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = 2000L
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setUpViewModel()
        setUpRecyclerViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun setUpRecyclerViews() {
        binding.myCoursesRecycler.also {
            it.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = MyCoursesAdapter(this)
        }
        binding.allCoursesRecycler.also {
            it.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)
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

    override fun onCourseItemClicked(view: View, course: CourseEntity) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = 400L
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 400L
        }
        val courseCardDetailTransitionName = getString(R.string.course_details_transition_name)
        val extras = FragmentNavigatorExtras(view to courseCardDetailTransitionName)
        val action = HomeFragmentDirections.actionNavHomeToCourseDetailsFragment(course.id)
        findNavController().navigate(action, extras)
    }
}