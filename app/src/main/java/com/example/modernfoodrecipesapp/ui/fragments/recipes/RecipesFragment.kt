package com.example.modernfoodrecipesapp.ui.fragments.recipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modernfoodrecipesapp.R
import com.example.modernfoodrecipesapp.data.database.RecipesEntity
import com.example.modernfoodrecipesapp.databinding.FragmentRecipesBinding
import com.example.modernfoodrecipesapp.ui.MainViewModel
import com.example.modernfoodrecipesapp.utils.Constants
import com.example.modernfoodrecipesapp.utils.NetworkResult
import com.example.modernfoodrecipesapp.utils.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private val recipesViewModel: RecipesViewModel by viewModels()
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private val recipesAdapter by lazy {
        RecipesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipes, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        readDatabase()
    }

    private fun setupRecyclerView() {
        binding.shimmerRecyclrerView.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner, { database ->
                if (database.isNotEmpty()) {
                    loadDataToAdapter(database)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            })
        }
    }

    private fun loadDataToAdapter(database: List<RecipesEntity>?) {
        database?.firstOrNull().let {
            recipesAdapter.submitList(it?.foodRecipe?.results)
        }
    }

    private fun requestApiData() {
        val queries = recipesViewModel.applyQueries()
        mainViewModel.getRecipes(queries = queries)
        mainViewModel.recipesResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { foodRecipe ->
                        recipesAdapter.submitList(foodRecipe.results)
                    }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        })
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner, { database ->
                if (database.isNotEmpty()) {
                    loadDataToAdapter(database)
                }
            })
        }
    }

    private fun showShimmerEffect() {
        binding.shimmerRecyclrerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.shimmerRecyclrerView.hideShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}