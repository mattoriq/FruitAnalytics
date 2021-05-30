package com.akiramenaide.capstoneproject.core.domain.usecase

import com.akiramenaide.capstoneproject.core.domain.model.Fruit
import com.akiramenaide.capstoneproject.core.domain.repository.IFruitRepository
import kotlinx.coroutines.flow.Flow

class FruitInteractor(private val fruitRepository: IFruitRepository): FruitUseCase {
    override fun getAllFruits(): Flow<List<Fruit>> = fruitRepository.getAllFruits()

    override fun insertFruit(fruit: Fruit) = fruitRepository.insertFruit(fruit)

    override fun updateFruitInfo(fruit: Fruit) = fruitRepository.updateFruitInfo(fruit)
}