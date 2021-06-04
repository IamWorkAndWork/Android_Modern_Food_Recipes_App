package com.example.modernfoodrecipesapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.example.foody.models.FoodRecipe
import com.example.modernfoodrecipesapp.data.Repository
import com.example.modernfoodrecipesapp.data.database.RecipesEntity
import com.example.modernfoodrecipesapp.data.database.entities.FavoritesEntity
import com.example.modernfoodrecipesapp.data.database.entities.FoodJokeEntity
import com.example.modernfoodrecipesapp.models.FoodJoke
import com.example.modernfoodrecipesapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) :
    AndroidViewModel(application) {

    //    ROOM DATABASE
    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()
    val readFavoriteRecipes = repository.local.readFavoriteRecipes().asLiveData()
    val readFoodJoke = repository.local.readFoodJoke().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity = recipesEntity)
        }

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipes(favoritesEntity)
        }
    }

    private fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFoodJoke(foodJokeEntity)
        }
    }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }
    }

    fun deleteAllFavoriteRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }
    }

    //    RETROFIT
    var recipesResponse = MutableLiveData<NetworkResult<FoodRecipe>>()
    var searchedRecipesResponse = MutableLiveData<NetworkResult<FoodRecipe>>()
    var foodJokeResponse = MutableLiveData<NetworkResult<FoodJoke>>()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun searchRecipes(searchQuery: Map<String, String>) {
        viewModelScope.launch {
            searchRecipesSafeCall(searchQuery)
        }
    }

    fun getFoodJoke(apiKey: String) {
        viewModelScope.launch {
            getFoodJokeSafeCall(apiKey)
        }
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {

        recipesResponse.value = NetworkResult.Loading()

        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value?.data
                foodRecipe?.let {
                    offLineCacheRecipes(it)
                }

            } catch (error: Exception) {
                recipesResponse.value = NetworkResult.Error("Recipes not found.")
            }

        } else {
            recipesResponse.value = NetworkResult.Error("No Internet Connection")
        }

    }

    private suspend fun getFoodJokeSafeCall(apiKey: String) {
        foodJokeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getFoodJoke(apiKey)
                foodJokeResponse.value = handlerFoodJokeResponse(response)

                val foodJoke = foodJokeResponse.value?.data
                if (foodJoke != null) {
                    offLineCacheFoodJoke(foodJoke)
                }

            } catch (e: Exception) {
                foodJokeResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            foodJokeResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun offLineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe = foodRecipe)
        insertRecipes(recipesEntity = recipesEntity)
    }

    private fun offLineCacheFoodJoke(foodJoke: FoodJoke) {
        val foodJokeEntity = FoodJokeEntity(foodJoke)
        insertFoodJoke(foodJokeEntity)
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchedRecipesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                searchedRecipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API KEY Limited.")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handlerFoodJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val foodJoke = response.body()
                NetworkResult.Success(foodJoke)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}