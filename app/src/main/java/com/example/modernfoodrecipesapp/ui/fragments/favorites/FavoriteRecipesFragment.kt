package com.example.modernfoodrecipesapp.ui.fragments.favorites

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modernfoodrecipesapp.R
import com.example.modernfoodrecipesapp.databinding.FragmentFavoriteRecipesBinding
import com.example.modernfoodrecipesapp.ui.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {

    private val mainViewModel by viewModels<MainViewModel>()
    private var _binding: FragmentFavoriteRecipesBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        FavoriteRecipesAdapter(
            requireActivity(),
            mainViewModel
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.mAdapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        setupRecyclerView(binding.favoriteRecipesRecyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_recipes_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteAll_favorite_recipes_menu) {
            mainViewModel.deleteAllFavoriteRecipes()
            showSnackBar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSnackBar() {
        Snackbar.make(
            binding.root,
            "All recipes removed.",
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    private fun setupRecyclerView(favoriteRecipesRecyclerView: RecyclerView) {
        favoriteRecipesRecyclerView.apply {
            adapter = this@FavoriteRecipesFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter.clearContextualActionMode()
    }

}