package com.example.map.googlemap.network.response

import com.example.map.googlemap.utils.*
import com.google.gson.annotations.SerializedName

data class ReverseGeocodeResponse(
    @SerializedName(RESULTS)
    val results: List<Result?>?
)  {
    data class Result(
        @SerializedName(FORMATTED_ADDRESS)
        val formattedAddress: String?,
        @SerializedName(GEOMETRY)
        val geometry: Geometry?,
        @SerializedName(TYPES)
        val types: List<String?>?
    ) {

        data class Geometry(
            @SerializedName(LOCATION)
            val location: Location?
        ) {

            data class Location(
                @SerializedName(LAT)
                val lat: Double,
                @SerializedName(LNG)
                val lng: Double
            )
        }
    }
}