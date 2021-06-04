package com.example.modernfoodrecipesapp.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modernfoodrecipesapp.R
import com.example.modernfoodrecipesapp.data.database.RecipesEntity
import com.example.modernfoodrecipesapp.databinding.FragmentRecipesBinding
import com.example.modernfoodrecipesapp.ui.MainViewModel
import com.example.modernfoodrecipesapp.utils.NetworkListener
import com.example.modernfoodrecipesapp.utils.NetworkResult
import com.example.modernfoodrecipesapp.utils.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {
    private val args by navArgs<RecipesFragmentArgs>()

    private val mainViewModel: MainViewModel by viewModels()
    private val recipesViewModel: RecipesViewModel by activityViewModels()
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private val recipesAdapter by lazy {
        RecipesAdapter()
    }

    private lateinit var networkListener: NetworkListener

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

        setHasOptionsMenu(true)
        setupRecyclerView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner, {
            recipesViewModel.backOnline = it
        })

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailalibility(requireContext()).collect { status ->
                Log.d("NetworkListener", status.toString())
                recipesViewModel.networkStatus = status
                recipesViewModel.showNetworkStatus()
                readDatabase()
            }
        }

        binding.recipesFab.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
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
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
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
                    recipesViewModel.saveMealAndDietType()
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun searchApiData(searchQuery: String) {
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery = searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    val foodRecipe = response.data
                    foodRecipe?.let {
                        recipesAdapter.submitList(it.results)
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

}