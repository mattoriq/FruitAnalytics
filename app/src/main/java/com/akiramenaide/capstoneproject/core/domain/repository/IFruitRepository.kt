package com.akiramenaide.capstoneproject.core.domain.repository

import com.akiramenaide.capstoneproject.core.domain.model.Fruit
import kotlinx.coroutines.flow.Flow

interface IFruitRepository {
    fun getAllFruits(): Flow<List<Fruit>>

    fun insertFruit(fruit: Fruit)

    fun updateFruitInfo(fruit: Fruit)
}