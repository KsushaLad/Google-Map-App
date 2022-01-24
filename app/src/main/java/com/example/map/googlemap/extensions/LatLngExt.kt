package com.example.map.googlemap.extensions

import com.google.android.gms.maps.model.LatLng


fun LatLng.toParam(): String {
    latitude
    longitude
    return StringBuilder(60).append(latitude).append(",").append(longitude).toString()
}