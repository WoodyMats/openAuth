package com.woodymats.openauth.ui.courseContentView

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

    private lateinit var viewModel: CourseContentSharedViewModel
    private lateinit var binding: FragmentCourseContentBinding
    private val args: CourseContentFragmentArgs by navArgs()
    private val contentId: Long by lazy(LazyThreadSafetyMode.NONE) { args.contentId }
    private var fragmentContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCourseContentBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
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
        val viewModelFactory = CourseContentViewModelFactory(
            fragmentContext!!.getSharedPreferences(
                PREFERENCES,
                Context.MODE_PRIVATE
            ), getInstance(fragmentContext!!), contentId
        )
        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(CourseContentSharedViewModel::class.java)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        viewModel.currentContent.observe(viewLifecycleOwner, {
            if (it != null) {
                if (!it.content.isNullOrEmpty()) {
                    binding.webView.settings.allowContentAccess = true
                    binding.webView.settings.allowUniversalAccessFromFileURLs = true
                    binding.webView.settings.javaScriptEnabled = true
                    binding.webView.settings.loadWithOverviewMode = true
                    binding.webView.settings.useWideViewPort = true
                    binding.webView.settings.setSupportZoom(true)
                    // "<style>*{display: inline;height: auto;max-width: 100%;}</style><div class=\"body\">" +
                    binding.webView.loadDataWithBaseURL(
                        null,
                        setToCenter("img") + setToCenter("iframe") + it.content,
                        "text/html; charset=utf-8",
                        "UTF-8",
                        null
                    )
                }
            }
        })
    }

    private fun setToCenter(element: String): String {
        return "<style>$element{display: block; margin-left: auto; margin-right: auto; max-width: 100%;}</style>"
    }

    private fun setUpListeners() {
        // TODO("Nothing to do yet!")
    }
}