package com.example.modernfoodrecipesapp.ui.fragments.recipes.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.modernfoodrecipesapp.R
import com.example.modernfoodrecipesapp.databinding.RecipesBottomSheetBinding
import com.example.modernfoodrecipesapp.databinding.RecipesRowLayoutBinding
import com.example.modernfoodrecipesapp.ui.fragments.recipes.RecipesViewModel
import com.example.modernfoodrecipesapp.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class RecipesBottomSheet : BottomSheetDialogFragment() {

    private var _binding: RecipesBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var mealTypeChip = Constants.DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = Constants.DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    private val recipesViewModel by activityViewModels<RecipesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RecipesBottomSheetBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner, { value ->
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            updateChip(value.selectedMealTypeId, binding.mealTypeChipGroup)
            updateChip(value.selectedDietTypeId, binding.dietTypeChipGroup)
        })

        binding.mealTypeChipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedMealType = chip.text.toString().lowercase(Locale.ROOT)
            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId
        }

        binding.dietTypeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val selectedDietType = chip.text.toString().lowercase(Locale.ROOT)
            dietTypeChip = selectedDietType
            dietTypeChipId = checkedId
        }

        binding.applyBtn.setOnClickListener {
            recipesViewModel.saveMealAndDietTypeTemp(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )
            val action =
                RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
            findNavController().navigate(action)
        }

    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                val targetView = chipGroup.findViewById<Chip>(chipId)
                targetView.isChecked = true
                chipGroup.requestChildFocus(targetView, targetView)
            } catch (e: Exception) {
                Log.d("RecipesBottomSheet", e.message.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}