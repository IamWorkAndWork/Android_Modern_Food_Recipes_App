package com.example.modernfoodrecipesapp.ui.fragments.instructions

import android.os.Bundle
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.foody.models.Result
import com.example.modernfoodrecipesapp.databinding.FragmentInstructionsBinding
import com.example.modernfoodrecipesapp.utils.Constants

class InstructionsFragment : Fragment() {

    private var _binding: FragmentInstructionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstructionsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val myBundle: Result? = args?.getParcelable(Constants.RECIPE_RESULT_KEY)

        binding.instructionsWebView.webViewClient = object : WebViewClient() {

        }

        val websiteUrl = myBundle?.sourceUrl
        websiteUrl?.let { binding.instructionsWebView.loadUrl(it) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}