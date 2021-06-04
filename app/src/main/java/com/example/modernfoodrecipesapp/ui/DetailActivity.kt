package com.example.modernfoodrecipesapp.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.example.foody.models.Result
import com.example.modernfoodrecipesapp.R
import com.example.modernfoodrecipesapp.data.database.entities.FavoritesEntity
import com.example.modernfoodrecipesapp.databinding.ActivityDetailBinding
import com.example.modernfoodrecipesapp.ui.fragments.PagerAdapter
import com.example.modernfoodrecipesapp.ui.fragments.ingredients.IngredientsFragment
import com.example.modernfoodrecipesapp.ui.fragments.instructions.InstructionsFragment
import com.example.modernfoodrecipesapp.ui.fragments.overview.OverviewFragment
import com.example.modernfoodrecipesapp.utils.Constants.Companion.RECIPE_RESULT_KEY
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val args by navArgs<DetailActivityArgs>()
    private val mainViewModel by viewModels<MainViewModel>()

    private var recipeSaved = false
    private var savedRecipeId = 0

    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = arrayListOf<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        val titles = arrayListOf<String>(
            "Overview", "Ingredients", "Instructions"
        )

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result)

        val pagerAdapter = PagerAdapter(
            resultBundle,
            fragments,
            this
        )

        binding.viewPager2.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = titles[position]
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        menuItem = menu!!.findItem(R.id.save_to_favorites_menu)
        checkSavedRecipes(menuItem)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.save_to_favorites_menu && !recipeSaved) {
            saveToFavorites(item)
        } else if (item.itemId == R.id.save_to_favorites_menu && recipeSaved) {
            removeFromFavorites(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun removeFromFavorites(item: MenuItem) {
        val favoeitesEntity = FavoritesEntity(savedRecipeId, args.result)
        mainViewModel.deleteFavoriteRecipe(favoeitesEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackBar("Removed from Favorites.")
        recipeSaved = false
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.detailsLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    private fun checkSavedRecipes(menuItem: MenuItem?) {
        mainViewModel.readFavoriteRecipes.observe(this, { favoritesEntity ->
            try {
                for (savedRecipe in favoritesEntity) {
                    val result: Result = args.result
                    if (savedRecipe.result.id == result.id) {
                        menuItem?.let { changeMenuItemColor(it, R.color.yellow) }
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        })
    }

    private fun saveToFavorites(item: MenuItem) {
        val favoritesEntity =
            FavoritesEntity(
                0,
                args.result
            )
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Recipe saved.")
        recipeSaved = true
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon.setTint(ContextCompat.getColor(this, color))
    }

    override fun onDestroy() {
        super.onDestroy()
        changeMenuItemColor(menuItem, R.color.white)
    }

}