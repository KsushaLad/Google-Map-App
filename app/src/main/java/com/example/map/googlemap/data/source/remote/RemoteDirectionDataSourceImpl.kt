package com.example.map.googlemap.data.source.remote

import com.example.map.googlemap.data.source.DirectionDataSource
import com.example.map.googlemap.extensions.networkDispatchToMainThread
import com.example.map.googlemap.network.api.DirectionAPI
import com.example.map.googlemap.network.response.DirectionResponse
import io.reactivex.Single

class RemoteDirectionDataSourceImpl(private val directionAPI: DirectionAPI) : DirectionDataSource {


    override  fun getDriveCourse(origin: String, destination: String): Single<DirectionResponse> =
        directionAPI.getDrivingCourse(origin, destination).networkDispatchToMainThread()

}