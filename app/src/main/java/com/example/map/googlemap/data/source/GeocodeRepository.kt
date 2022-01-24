package com.example.map.googlemap.data.source

import com.example.map.googlemap.data.source.remote.RemoteGeocodeDataSourceImpl
import com.example.map.googlemap.network.response.GeocodeResponse
import com.example.map.googlemap.network.response.PlaceResponse
import com.example.map.googlemap.network.response.ReverseGeocodeResponse
import io.reactivex.Single

class GeocodeRepository(private val remoteGeocodeDataSourceImpl: RemoteGeocodeDataSourceImpl) : GeocodeDataSource {

    override fun getLocationUseAddress(address: String): Single<GeocodeResponse> =
        remoteGeocodeDataSourceImpl.getLocationUseAddress(address)

    override fun getLocationUseLatLng(latLng: String): Single<ReverseGeocodeResponse> =
        remoteGeocodeDataSourceImpl.getLocationUseLatLng(latLng)

    override fun getPlace(address: String): Single<PlaceResponse> =
        remoteGeocodeDataSourceImpl.getPlace(address)
}