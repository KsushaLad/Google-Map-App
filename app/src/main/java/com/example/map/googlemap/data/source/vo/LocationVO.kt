package com.example.map.googlemap.data.source.vo

import com.google.android.gms.maps.model.LatLng

data class LocationVO (
    val latLng: LatLng,
    val addressName: String? = "",
    val name: String? = ""
)