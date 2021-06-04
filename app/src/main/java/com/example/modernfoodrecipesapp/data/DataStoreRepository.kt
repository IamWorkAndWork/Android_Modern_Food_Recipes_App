package com.example.modernfoodrecipesapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.modernfoodrecipesapp.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.modernfoodrecipesapp.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.modernfoodrecipesapp.utils.Constants.Companion.PREFERENCES_BACK_ONLINE
import com.example.modernfoodrecipesapp.utils.Constants.Companion.PREFERENCES_DIET_TYPE
import com.example.modernfoodrecipesapp.utils.Constants.Companion.PREFERENCES_DIET_TYPE_ID
import com.example.modernfoodrecipesapp.utils.Constants.Companion.PREFERENCES_MEAL_TYPE
import com.example.modernfoodrecipesapp.utils.Constants.Companion.PREFERENCES_MEAL_TYPE_ID
import com.example.modernfoodrecipesapp.utils.Constants.Companion.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

interface DataStoreRepository {
    suspend fun saveMealAndDietType(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    )

    suspend fun saveBackOnline(backOnline: Boolean)
    fun readMealAndDietType(): Flow<MealAndDietType>
    fun readBackOnline(): Flow<Boolean>
}

@ActivityRetainedScoped
class DataStoreRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DataStoreRepository {

    private object PreferenceKeys {
        val selectedMealType = stringPreferencesKey(PREFERENCES_MEAL_TYPE)
        val selectedMealTypeId = intPreferencesKey(PREFERENCES_MEAL_TYPE_ID)
        val selectedDietType = stringPreferencesKey(PREFERENCES_DIET_TYPE)
        val selectedDietTypeId = intPreferencesKey(PREFERENCES_DIET_TYPE_ID)
        val backOnline = booleanPreferencesKey(PREFERENCES_BACK_ONLINE)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    override suspend fun saveMealAndDietType(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        dataStore.edit { preferences ->
            preferences.set(PreferenceKeys.selectedMealType, mealType)
            preferences.set(PreferenceKeys.selectedMealTypeId, mealTypeId)
            preferences.set(PreferenceKeys.selectedDietType, dietType)
            preferences.set(PreferenceKeys.selectedDietTypeId, dietTypeId)
        }
    }

    override suspend fun saveBackOnline(backOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }

    override fun readMealAndDietType(): Flow<MealAndDietType> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
            .map { preferences ->

                val selectedMealType =
                    preferences[PreferenceKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
                val selectedMealTypeId = preferences[PreferenceKeys.selectedMealTypeId] ?: 0
                val selectedDietType =
                    preferences[PreferenceKeys.selectedDietType] ?: DEFAULT_DIET_TYPE
                val selectedDietTypeId = preferences[PreferenceKeys.selectedDietTypeId] ?: 0

                MealAndDietType(
                    selectedMealType,
                    selectedMealTypeId,
                    selectedDietType,
                    selectedDietTypeId
                )

            }
    }

    override fun readBackOnline(): Flow<Boolean> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
            .map { preferences ->
                val backOnLine = preferences[PreferenceKeys.backOnline] ?: false
                backOnLine
            }
    }

}

data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)