package com.woodymats.openauth.ui.courseContentsList

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.woodymats.openauth.adapters.CourseContentsListAdapter
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.databinding.FragmentCourseContentsListBinding
import com.woodymats.openauth.models.local.ContentEntity
import com.woodymats.openauth.utils.PREFERENCES

/**
 * A fragment representing a list of Items.
 */
class CourseContentsListFragment : Fragment(), ContentsRecyclerViewClickListener {

    private lateinit var viewModel: CourseContentSharedViewModel
    private lateinit var binding: FragmentCourseContentsListBinding
    private val args: CourseContentsListFragmentArgs by navArgs()
    private val chapterId: Long by lazy(LazyThreadSafetyMode.NONE) { args.chapterId }
    private var fragmentContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseContentsListBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.setChapterId(chapterId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onDetach() {
        fragmentContext = null
        super.onDetach()
    }

    private fun setUpRecyclerView() {
        binding.contentsRecyclerView.also {
            it.layoutManager =
                LinearLayoutManager(fragmentContext, LinearLayoutManager.VERTICAL, false)
            it.adapter = CourseContentsListAdapter(this)
        }
    }

    private fun setUpViewModel() {
        val viewModelFactory = CourseContentSharedViewModelFactory(
            fragmentContext!!.getSharedPreferences(
                PREFERENCES,
                Context.MODE_PRIVATE
            ), getInstance(fragmentContext!!)
        )
        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(CourseContentSharedViewModel::class.java)
    }

    override fun onContentItemClicked(view: View, content: ContentEntity) {
        viewModel.setContentAsCompleted(content.id)
        val action =
            CourseContentsListFragmentDirections.actionCourseContentsListFragmentToCourseContentFragment(
                content.title,
                content.chapterId
            )
        findNavController().navigate(action)
    }
}