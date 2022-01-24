package com.example.map.googlemap.data.source

import com.example.map.googlemap.data.source.vo.LocationVO

interface LocalSearchPlaceDataSource {
    fun saveLocationVOList(locationVO: LocationVO, callBack: () -> Unit)
    fun loadLocationVOList(): List<LocationVO>?
    fun clearLocationVOList(callBack: () -> Unit)
}