package com.akiramenaide.capstoneproject.core.domain.usecase

import com.akiramenaide.capstoneproject.core.domain.model.Fruit
import kotlinx.coroutines.flow.Flow

interface FruitUseCase {
    fun getAllFruits(): Flow<List<Fruit>>
    fun insertFruit(fruit: Fruit)
    fun updateFruitInfo(fruit: Fruit)
}