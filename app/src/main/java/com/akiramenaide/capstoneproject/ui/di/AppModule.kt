package com.akiramenaide.capstoneproject.ui.di

import com.akiramenaide.capstoneproject.core.domain.usecase.FruitInteractor
import com.akiramenaide.capstoneproject.core.domain.usecase.FruitUseCase
import com.akiramenaide.capstoneproject.ui.detail.DetailViewModel
import com.akiramenaide.capstoneproject.ui.home.HomeViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<FruitUseCase> { FruitInteractor(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { DetailViewModel(get()) }
}