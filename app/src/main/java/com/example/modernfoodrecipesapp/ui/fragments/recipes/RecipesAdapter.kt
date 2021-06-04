package com.example.modernfoodrecipesapp.ui.fragments.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.models.Result
import com.example.modernfoodrecipesapp.databinding.RecipesRowLayoutBinding

class RecipesAdapter : ListAdapter<Result, RecyclerView.ViewHolder>(diffItem()) {

    companion object {
        private fun diffItem() = object : DiffUtil.ItemCallback<Result>() {
            override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(getItem(position))
    }

    class ItemViewHolder(private val binding: RecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }

        fun bind(result: Result) {
            binding.result = result
            binding.executePendingBindings()
        }

    }

}