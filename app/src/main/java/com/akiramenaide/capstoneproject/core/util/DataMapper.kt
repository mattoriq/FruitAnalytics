package com.akiramenaide.capstoneproject.core.util

import com.akiramenaide.capstoneproject.core.data.source.local.entity.FruitEntity
import com.akiramenaide.capstoneproject.core.domain.model.Fruit

object DataMapper {
    fun mapEntitiesToDomain(input: List<FruitEntity>): List<Fruit> =
        input.map {
            Fruit(
                id = it.id,
                name = it.name,
                total = it.total,
                freshTotal = it.freshTotal
            )
        }

    fun mapDomainToEntity(input: Fruit) =
        FruitEntity(
            id = input.id,
            name = input.name,
            total = input.total,
            freshTotal = input.freshTotal
        )
}