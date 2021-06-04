package com.example.modernfoodrecipesapp.ui.fragments.recipes

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.modernfoodrecipesapp.data.DataStoreRepository
import com.example.modernfoodrecipesapp.data.MealAndDietType
import com.example.modernfoodrecipesapp.utils.Constants.Companion.API_KEY
import com.example.modernfoodrecipesapp.utils.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_API_KEY
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_DIET
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_NUMBER
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_SEARCH
import com.example.modernfoodrecipesapp.utils.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    private var mealAndDiet: MealAndDietType? = null

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType()
    val readBackOnline = dataStoreRepository.readBackOnline().asLiveData()

    fun saveMealAndDietType() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(
                mealAndDiet?.selectedMealType.orEmpty(),
                mealAndDiet?.selectedMealTypeId ?: 0,
                mealAndDiet?.selectedDietType.orEmpty(),
                mealAndDiet?.selectedDietTypeId ?: 0
            )
        }
    }

    fun saveMealAndDietTypeTemp(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        mealAndDiet = MealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId
        )
    }

    private fun saveBackOnline(backOnline: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }
    }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealAndDiet?.selectedMealType ?: ""
        queries[QUERY_DIET] = mealAndDiet?.selectedDietType ?: ""
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries = hashMapOf<String, String>(
            QUERY_SEARCH to searchQuery,
            QUERY_NUMBER to DEFAULT_RECIPES_NUMBER,
            QUERY_API_KEY to API_KEY,
            QUERY_ADD_RECIPE_INFORMATION to "true",
            QUERY_FILL_INGREDIENTS to "true"
        )
        return queries
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else {
            Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
            saveBackOnline(false)
        }
    }

}
