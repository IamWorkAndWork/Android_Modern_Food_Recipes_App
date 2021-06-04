package com.example.modernfoodrecipesapp.data.database

import androidx.room.*
import com.example.modernfoodrecipesapp.data.database.entities.FavoritesEntity
import com.example.modernfoodrecipesapp.data.database.entities.FoodJokeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity)

    @Query("select * from recipes_table order by id asc")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("select * from favorite_recipes_table order by id asc")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("select * from food_joke_table order by id asc")
    fun readFoodJoke(): Flow<List<FoodJokeEntity>>

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("delete from favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()

}