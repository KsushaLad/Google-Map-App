package com.example.map.googlemap.data.source.vo

import com.google.android.gms.maps.model.LatLng

data class DirectionVO ( //расстояние
    val latLng: List<LatLng>,
    val time: String?,
    val distance: String?,
    val key: Int
)