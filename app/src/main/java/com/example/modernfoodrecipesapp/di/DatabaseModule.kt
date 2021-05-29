package com.example.modernfoodrecipesapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.modernfoodrecipesapp.data.LocalDataSource
import com.example.modernfoodrecipesapp.data.database.RecipesDao
import com.example.modernfoodrecipesapp.data.database.RecipesDatabase
import com.example.modernfoodrecipesapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): RecipesDatabase {
        return Room.databaseBuilder(
            context, RecipesDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideDao(database: RecipesDatabase): RecipesDao {
        return database.recipesDao()
    }

}