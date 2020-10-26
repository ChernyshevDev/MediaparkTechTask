package com.example.mediaparktechtask.presentation.expanded_news

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.mediaparktechtask.R
import com.example.mediaparktechtask.databinding.ItemNewsExpandedCardBinding
import com.example.mediaparktechtask.presentation.ViewEventHelper

const val IMAGE = "image"
const val TITLE = "title"
const val CONTENT = "content"
const val WEB_LINK = "link"

class ExpandedNewsFragment : DialogFragment() {

    private lateinit var binding: ItemNewsExpandedCardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ItemNewsExpandedCardBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.attributes?.windowAnimations = R.style.MyDialogAnimation
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(requireContext().getDrawable(R.drawable.color_transparent))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        bundle?.let {
            val image = it.getByteArray(IMAGE)?.toBitmap()
            val title = it.getString(TITLE)
            val content = it.getString(CONTENT)
            val linkToSource = it.getString(WEB_LINK)

            with(binding) {
                expandedNewsImage.setImageBitmap(image)
                expandedNewsTitle.text = title
                expandedNewsContent.text = content

                expandedNewsReadMoreButton.setOnClickListener {
                    navigateToWebView(linkToSource!!)
                }
            }
        }
    }

    private fun navigateToWebView(link: String) {
        findNavController().navigate(R.id.action_expanded_news_to_web_view, Bundle().apply {
            putString(WEB_LINK, link)
        })
        ViewEventHelper.hideSearchBar()
    }

    private fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }
}