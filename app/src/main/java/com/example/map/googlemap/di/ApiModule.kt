package com.example.map.googlemap.di

import com.example.map.googlemap.network.api.DirectionAPI
import com.example.map.googlemap.network.api.GeocodeAPI
import com.example.map.googlemap.utils.DI_API_NO_AUTH
import com.example.map.googlemap.utils.DI_RETROFIT_NO_AUTH

import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    single(named(DI_API_NO_AUTH)) { (get(named(DI_RETROFIT_NO_AUTH)) as Retrofit).create(GeocodeAPI::class.java) }
    single(named(DI_API_NO_AUTH)) { (get(named(DI_RETROFIT_NO_AUTH)) as Retrofit).create(DirectionAPI::class.java) }
}

