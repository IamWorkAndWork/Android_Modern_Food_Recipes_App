package com.example.modernfoodrecipesapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foody.models.Result
import com.example.modernfoodrecipesapp.utils.Constants

@Entity(tableName = Constants.FAVORITE_RECIPES_TABLE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val result: Result
)