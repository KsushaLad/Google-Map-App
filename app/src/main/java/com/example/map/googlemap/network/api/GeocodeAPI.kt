package com.example.map.googlemap.network.api

import com.example.map.googlemap.network.response.GeocodeResponse
import com.example.map.googlemap.network.response.PlaceResponse
import com.example.map.googlemap.network.response.ReverseGeocodeResponse
import com.example.map.googlemap.utils.ADDRESS
import com.example.map.googlemap.utils.LATLNG
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeAPI {

    companion object{
        const val geocode = "geocode/json"
    }

    @GET(geocode)
    fun getLocationUseAddress(@Query(ADDRESS) address: String): Single<GeocodeResponse>

    @GET(geocode)
    fun getLocationUseLatLng(@Query(LATLNG) latLng: String): Single<ReverseGeocodeResponse>

    @GET(geocode)
    fun getPlace(@Query(ADDRESS) address: String) : Single<PlaceResponse>

}