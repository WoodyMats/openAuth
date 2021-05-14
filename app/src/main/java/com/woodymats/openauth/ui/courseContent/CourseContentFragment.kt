package com.woodymats.openauth.ui.courseContent

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.databinding.FragmentCourseContentBinding
import com.woodymats.openauth.utils.PREFERENCES

class CourseContentFragment : Fragment() {

    private lateinit var viewModel: CourseContentViewModel
    private lateinit var binding: FragmentCourseContentBinding
    private val args: CourseContentFragmentArgs by navArgs()
    private val chapterId: Long by lazy(LazyThreadSafetyMode.NONE) { args.chapterId }
    private var fragmentContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCourseContentBinding.inflate(layoutInflater)
        setUpViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        setUpWebView()
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
        val viewModelFactory = CourseContentViewModelFactory(fragmentContext!!.getSharedPreferences(
            PREFERENCES,
            Context.MODE_PRIVATE
        ), getInstance(fragmentContext!!), chapterId)
        binding.viewModel = ViewModelProvider(this, viewModelFactory).get(CourseContentViewModel::class.java)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CourseContentViewModel::class.java)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        viewModel.currentContent.observe(viewLifecycleOwner, {
            if (it != null) {
                if (!it.content.isNullOrEmpty()) {
                    binding.webView.settings.allowContentAccess = true
                    binding.webView.settings.allowUniversalAccessFromFileURLs = true
                    binding.webView.settings.javaScriptEnabled = true
                    binding.webView.loadData(it.content, "text/html; charset=utf-8", "UTF-8")
                }
            }
        })
    }

    private fun setUpListeners() {
        // TODO("Nothing to do yet!")
    }
}