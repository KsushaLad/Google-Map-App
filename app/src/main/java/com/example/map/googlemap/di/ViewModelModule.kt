package com.example.map.googlemap.di


import com.example.map.googlemap.vm.MapViewModel
import com.example.map.googlemap.vm.SearchLocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MapViewModel(get(), get()) }
    viewModel { SearchLocationViewModel(get(), get()) }
}