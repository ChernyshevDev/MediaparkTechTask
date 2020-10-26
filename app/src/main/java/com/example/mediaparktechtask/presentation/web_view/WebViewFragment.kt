package com.example.mediaparktechtask.presentation.web_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediaparktechtask.R
import com.example.mediaparktechtask.databinding.FragmentNewsWebviewBinding
import com.example.mediaparktechtask.presentation.ViewEventHelper
import com.example.mediaparktechtask.presentation.expanded_news.WEB_LINK

class WebViewFragment : Fragment() {

    private lateinit var viewBinding: FragmentNewsWebviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentNewsWebviewBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val webPageLink = bundle!!.getString(WEB_LINK)

        overrideDeviceBackButton()
        setupWebView(webPageLink!!)
    }

    private fun setupWebView(link: String) {
        viewBinding.webView.loadUrl(link)
        viewBinding.webView.removeLoadingAnimationOnPageLoaded()
    }

    private fun WebView.removeLoadingAnimationOnPageLoaded() {
        this.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                viewBinding.loadingAnimation.it.visibility = View.GONE
            }
        }
    }

    private fun overrideDeviceBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (findNavController().previousBackStackEntry!!.destination.id == R.id.search_page)
                        ViewEventHelper.showSearchBar()

                    findNavController().popBackStack()
                }
            })
    }
}