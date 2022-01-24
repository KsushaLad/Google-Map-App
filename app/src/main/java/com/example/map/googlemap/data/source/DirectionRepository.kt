package com.example.map.googlemap.data.source

import com.example.map.googlemap.data.source.remote.RemoteDirectionDataSourceImpl
import com.example.map.googlemap.network.response.DirectionResponse
import io.reactivex.Single

class DirectionRepository(private val remoteDirectionDataSourceImpl: RemoteDirectionDataSourceImpl) : DirectionDataSource {

    override fun getDriveCourse(origin: String, destination: String): Single<DirectionResponse> =
        remoteDirectionDataSourceImpl.getDriveCourse(origin, destination)
}