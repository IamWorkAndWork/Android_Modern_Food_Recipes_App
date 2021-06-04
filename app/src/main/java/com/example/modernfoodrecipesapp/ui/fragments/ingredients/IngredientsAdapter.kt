package com.example.modernfoodrecipesapp.ui.fragments.ingredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.foody.models.ExtendedIngredient
import com.example.modernfoodrecipesapp.R
import com.example.modernfoodrecipesapp.databinding.IngredientsRowLayoutBinding
import com.example.modernfoodrecipesapp.utils.Constants
import com.example.modernfoodrecipesapp.utils.RecipesDiffUtil

class IngredientsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ingredientsList = emptyList<ExtendedIngredient>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            IngredientsRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(ingredientsList[position])
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    fun setData(newIngredients: List<ExtendedIngredient>) {
        val ingredientsDiffUtil = RecipesDiffUtil(ingredientsList, newIngredients)
        val diffUtilResult = DiffUtil.calculateDiff(ingredientsDiffUtil)
        ingredientsList = newIngredients
        diffUtilResult.dispatchUpdatesTo(this)
    }

    class ItemViewHolder(private val binding: IngredientsRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(extendedIngredient: ExtendedIngredient) = with(binding) {
            ingredientImageView.load(Constants.BASE_IMAGE_URL + extendedIngredient.image) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
            ingredientName.text = extendedIngredient.amount.toString()
            ingredientUnit.text = extendedIngredient.unit
            ingredientConsistency.text = extendedIngredient.consistency
            ingredientOriginal.text = extendedIngredient.original
        }
    }
}