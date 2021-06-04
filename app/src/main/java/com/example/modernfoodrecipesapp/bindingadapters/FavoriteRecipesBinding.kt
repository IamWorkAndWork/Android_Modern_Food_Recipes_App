package com.example.modernfoodrecipesapp.bindingadapters

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.modernfoodrecipesapp.data.database.entities.FavoritesEntity
import com.example.modernfoodrecipesapp.ui.fragments.favorites.FavoriteRecipesAdapter

class FavoriteRecipesBinding {

    companion object {

        @BindingAdapter("setVisibility", "setData", requireAll = false)
        @JvmStatic
        fun setVisibility(
            view: View,
            favoritesEntity: List<FavoritesEntity>?,
            mAdapter: FavoriteRecipesAdapter?
        ) {
            when (view) {
                is RecyclerView -> {
                    val dataCheck = favoritesEntity.isNullOrEmpty()
                    view.isInvisible = dataCheck
                    if (!dataCheck) {
                        favoritesEntity?.let {
                            mAdapter?.setData(it)
                        }
                    }
                }
                else -> view.isVisible = favoritesEntity.isNullOrEmpty()
            }
        }

    }

}