package com.example.map.googlemap.data.source.remote

import com.example.map.googlemap.data.source.GeocodeDataSource
import com.example.map.googlemap.extensions.networkDispatchToMainThread
import com.example.map.googlemap.network.api.GeocodeAPI
import com.example.map.googlemap.network.response.GeocodeResponse
import com.example.map.googlemap.network.response.PlaceResponse
import com.example.map.googlemap.network.response.ReverseGeocodeResponse
import io.reactivex.Single


class RemoteGeocodeDataSourceImpl(private val geocodeAPI: GeocodeAPI) : GeocodeDataSource {


    override fun getLocationUseAddress(address: String): Single<GeocodeResponse> =
        geocodeAPI.getLocationUseAddress(address).networkDispatchToMainThread()

    override fun getPlace(address: String): Single<PlaceResponse> =
        geocodeAPI.getPlace(address).networkDispatchToMainThread()

    override fun getLocationUseLatLng(latLng: String): Single<ReverseGeocodeResponse> =
        geocodeAPI.getLocationUseLatLng(latLng).networkDispatchToMainThread()

}