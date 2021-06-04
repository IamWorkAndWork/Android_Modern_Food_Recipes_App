package com.example.modernfoodrecipesapp.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.modernfoodrecipesapp.models.FoodJoke
import com.example.modernfoodrecipesapp.utils.Constants

@Entity(tableName = Constants.FOOD_JOKE_TABLE)
class FoodJokeEntity(
    @Embedded
    var foodJoke: FoodJoke
) {

    @PrimaryKey(autoGenerate = false)
    var id: Int = 0

}