package com.example.map.googlemap.data.source

import com.example.map.googlemap.network.response.GeocodeResponse
import com.example.map.googlemap.network.response.PlaceResponse
import com.example.map.googlemap.network.response.ReverseGeocodeResponse
import io.reactivex.Single

interface GeocodeDataSource {

    fun getLocationUseAddress(address: String): Single<GeocodeResponse>
    fun getLocationUseLatLng(latLng: String): Single<ReverseGeocodeResponse>
    fun getPlace(address: String): Single<PlaceResponse>
}