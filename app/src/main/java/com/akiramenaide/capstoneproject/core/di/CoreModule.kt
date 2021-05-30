package com.akiramenaide.capstoneproject.core.di

import androidx.room.Room
import com.akiramenaide.capstoneproject.core.data.FruitRepository
import com.akiramenaide.capstoneproject.core.data.source.local.LocalDataSource
import com.akiramenaide.capstoneproject.core.data.source.local.room.FruitDatabase
import com.akiramenaide.capstoneproject.core.domain.repository.IFruitRepository
import com.akiramenaide.capstoneproject.core.util.AppExecutors
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    factory { get<FruitDatabase>().fruitDao() }
    single {
        Room.databaseBuilder(
            androidContext(),
            FruitDatabase::class.java, "Fruit.db"
        ).fallbackToDestructiveMigration().build()
    }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    factory { AppExecutors() }
    single<IFruitRepository> {
        FruitRepository(get(), get())
    }
}