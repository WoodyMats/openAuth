package com.woodymats.openauth.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.woodymats.openauth.adapters.AllCoursesAdapter
import com.woodymats.openauth.adapters.MyCoursesAdapter
import com.woodymats.openauth.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val viewModel: HomeFragmentViewModel by lazy {
        ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
    }
    private lateinit var binding: FragmentHomeBinding
    private var fragmentContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setUpViewModel()
        setUpRecyclerViews()
        return binding.root
    }

    private fun setUpRecyclerViews() {
        binding.myCoursesRecycler.also {
            it.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = MyCoursesAdapter()
        }
        binding.allCoursesRecycler.also {
            it.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)
            it.adapter = AllCoursesAdapter()
        }
        // viewModel.getUserEnrollments()
        // viewModel.enrollments.observe(viewLifecycleOwner, { enrollments ->
        //     binding.myCoursesRecycler.also {
        //         it.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)
        //         it.adapter = MyCoursesAdapter()
        //     }
        // })
    }

    private fun setUpViewModel() {
        binding.lifecycleOwner = this
        val application = requireActivity().application
        val viewModelFactory = HomeFragmentViewModelFactory(application)
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(HomeFragmentViewModel::class.java)
        // binding.executePendingBindings()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

}