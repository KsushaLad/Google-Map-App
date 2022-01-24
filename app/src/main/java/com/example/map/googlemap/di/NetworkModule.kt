package com.example.map.googlemap.di

import com.example.map.googlemap.BuildConfig
import com.example.map.googlemap.utils.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule: Module = module {
    single {
        GsonConverterFactory.create() as Converter.Factory
    }
    single {
     RxJava2CallAdapterFactory.create() as CallAdapter.Factory
    }
    single(named(DI_RETROFIT_NO_AUTH_CLIENT)) {
        OkHttpClient.Builder()
            .addInterceptor {
                val original = it.request()
                val request = original.newBuilder().apply {
                    url(
                        original.url().newBuilder().apply {
                            addQueryParameter(
                                KEY,
                                BuildConfig.GOOGLE_SECRET_KEY
                            )
                            addQueryParameter(
                                LANGUAGE,
                                BuildConfig.LANGUAGE
                            )
                        }.build()
                    )
                }.method(original.method(), original.body()).build()
                it.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .build()
    }
    single(named(DI_RETROFIT_NO_AUTH)) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(get(named(DI_RETROFIT_NO_AUTH_CLIENT)))
            .addConverterFactory(get())
            .addCallAdapterFactory(get())
            .build()
    }
}