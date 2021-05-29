package com.example.modernfoodrecipesapp.ui.fragments.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.modernfoodrecipesapp.utils.Constants.Companion.API_KEY
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_API_KEY
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_DIET
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_NUMBER
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_TYPE

class RecipesViewModel(application: Application) : AndroidViewModel(application) {

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = hashMapOf(
            QUERY_NUMBER to "30",
            QUERY_API_KEY to API_KEY,
            QUERY_TYPE to "snack",
            QUERY_DIET to "vegan",
            QUERY_ADD_RECIPE_INFORMATION to "true",
            QUERY_FILL_INGREDIENTS to "true"
        )
        return queries
    }

}