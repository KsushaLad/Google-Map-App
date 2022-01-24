package com.example.map.googlemap.network.response

import com.example.map.googlemap.utils.*
import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    @SerializedName(NEXT_PAGE_TOKEN)
    val nextPageToken: String?,
    @SerializedName(RESULTS)
    val results: List<ResultPlaceResponse>
){

    data class ResultPlaceResponse(
        @SerializedName(FORMATTED_ADDRESS)
        val formattedAddress: String?,
        @SerializedName(GEOMETRY)
        val geometry: GeometryPlaceResponse?,
        @SerializedName(NAME)
        val name: String
    ) {

        data class GeometryPlaceResponse(
            @SerializedName(LOCATION)
            val location: LocationPlaceResponse?
        ) {

            data class LocationPlaceResponse(
                @SerializedName(LAT)
                val lat: Double,
                @SerializedName(LNG)
                val lng: Double
            )
        }
    }
}