package com.example.map.googlemap.di

import com.example.map.googlemap.utils.DI_PREF_UTILS
import com.example.map.googlemap.utils.PrefUtils
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val prefUtilsModule = module {
    single(named(DI_PREF_UTILS)) { PrefUtils(androidApplication()) }
}