package com.akiramenaide.capstoneproject.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.akiramenaide.capstoneproject.core.domain.model.Fruit
import com.akiramenaide.capstoneproject.core.domain.usecase.FruitUseCase
import kotlinx.coroutines.launch

class HomeViewModel(private val fruitUseCase: FruitUseCase): ViewModel() {
    fun getAllFruits() = fruitUseCase.getAllFruits().asLiveData()

    fun insertFruit(fruit: Fruit) = fruitUseCase.insertFruit(fruit)

    fun updateFruitInfo(fruit: Fruit) = fruitUseCase.updateFruitInfo(fruit)
}