package com.example.map.googlemap.di

import com.example.map.googlemap.data.source.*
import com.example.map.googlemap.data.source.local.RecentSearchDataSourceImpl
import com.example.map.googlemap.data.source.remote.RemoteDirectionDataSourceImpl
import com.example.map.googlemap.data.source.remote.RemoteGeocodeDataSourceImpl
import com.example.map.googlemap.utils.DI_API_NO_AUTH
import com.example.map.googlemap.utils.DI_PREF_UTILS
import org.koin.core.qualifier.named
import org.koin.dsl.module


val dataSourceModel = module {
    single { RemoteGeocodeDataSourceImpl(get(named(DI_API_NO_AUTH))) }
    single { GeocodeRepository(get()) }
    single { RemoteDirectionDataSourceImpl(get(named(DI_API_NO_AUTH))) }
    single { DirectionRepository(get()) }
    single { RecentSearchDataSourceImpl(get(named(DI_PREF_UTILS))) }
    single { LocalSearchPlaceRepository(get()) }
}
