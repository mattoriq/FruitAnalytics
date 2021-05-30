package com.akiramenaide.capstoneproject.core.data

import android.util.Log
import com.akiramenaide.capstoneproject.core.data.source.local.LocalDataSource
import com.akiramenaide.capstoneproject.core.domain.model.Fruit
import com.akiramenaide.capstoneproject.core.domain.repository.IFruitRepository
import com.akiramenaide.capstoneproject.core.util.AppExecutors
import com.akiramenaide.capstoneproject.core.util.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FruitRepository(
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
    ): IFruitRepository {
    override fun getAllFruits(): Flow<List<Fruit>> =
        localDataSource.getAllFruits().map {
            DataMapper.mapEntitiesToDomain(it)
        }

    override fun insertFruit(fruit: Fruit) =
        appExecutors.diskIO().execute { localDataSource.insertFruit(DataMapper.mapDomainToEntity(fruit)) }

    override fun updateFruitInfo(fruit: Fruit) {
        val fruitEntity = DataMapper.mapDomainToEntity(fruit)
        Log.d("Repo", "${fruit.name}, ${fruit.total}, ${fruit.freshTotal}")
        Log.d("Repo", "${fruitEntity.name}, ${fruitEntity.total}, ${fruitEntity.freshTotal}")
        appExecutors.diskIO().execute {localDataSource.updateFruitInfo(fruitEntity) }

    }
}