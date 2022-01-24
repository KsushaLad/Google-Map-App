package com.example.map.googlemap.data.source

import com.example.map.googlemap.network.response.DirectionResponse
import io.reactivex.Single

interface DirectionDataSource {
    fun getDriveCourse(origin: String, destination: String): Single<DirectionResponse>
}