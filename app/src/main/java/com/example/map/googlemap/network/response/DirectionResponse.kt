package com.example.map.googlemap.network.response

import com.example.map.googlemap.utils.*
import com.google.gson.annotations.SerializedName

data class DirectionResponse(
    @SerializedName(ROUTES)
    val routes: List<Route?>?
    )


data class Route(
    @SerializedName(LEGS)
    val legs: List<Leg?>?
)


data class Leg(
    @SerializedName(DURATION)
    val duration: Duration?,
    @SerializedName(STEPS)
    val steps: List<Step?>?
)

data class Duration(
    @SerializedName(TEXT)
    val text: String?
)

data class Step(
    @SerializedName(DISTANCE)
    val distance: Distance?,
    @SerializedName(DURATION)
    val duration: Duration?,
    @SerializedName(POLYLINE)
    val polyline: Polyline
)

data class Distance(
    @SerializedName(TEXT)
    val text: String?
)

data class Polyline(
    @SerializedName(POINTS)
    val points: String
)
