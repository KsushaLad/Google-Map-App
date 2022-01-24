package com.example.map.googlemap.network.response

import com.example.map.googlemap.utils.*
import com.google.gson.annotations.SerializedName

data class GeocodeResponse(
    @SerializedName(RESULTS)
    val results: List<Result?>?,
    @SerializedName(STATUS)
    val status: String?
)

data class Result(
    @SerializedName(ADDRESS_COMPONENTS)
    val addressComponents: List<AddressComponent?>?,
    @SerializedName(FORMATTED_ADDRESS)
    val formattedAddress: String?,
    @SerializedName(GEOMETRY)
    val geometry: Geometry?,
    @SerializedName(PLACE_ID)
    val placeId: String?,
    @SerializedName(PLUS_CODE)
    val plusCode: PlusCode?,
    @SerializedName(TYPES)
    val types: List<String?>?
)

data class AddressComponent(
    @SerializedName(LONG_NAME)
    val longName: String?,
    @SerializedName(SHORT_NAME)
    val shortName: String?,
    @SerializedName(TYPES)
    val types: List<String?>?
)

data class Geometry(
    @SerializedName(LOCATION)
    val location: Location?,
    @SerializedName(LOCATION_TYPE)
    val locationType: String?,
    @SerializedName(VIEWPORT)
    val viewport: Viewport?
)

data class Location(
    @SerializedName(LAT)
    val lat: Double?,
    @SerializedName(LNG)
    val lng: Double?
)

data class Viewport(
    @SerializedName(NORTHEAST)
    val northeast: Northeast?,
    @SerializedName(SOUTHWEST)
    val southwest: Southwest?
)

data class Northeast(
    @SerializedName(LAT)
    val lat: Double?,
    @SerializedName(LNG)
    val lng: Double?
)

data class Southwest(
    @SerializedName(LAT)
    val lat: Double?,
    @SerializedName(LNG)
    val lng: Double?
)

data class PlusCode(
    @SerializedName(COMPOUND_CODE)
    val compoundCode: String?,
    @SerializedName(GLOBAL_CODE)
    val globalCode: String?
)
