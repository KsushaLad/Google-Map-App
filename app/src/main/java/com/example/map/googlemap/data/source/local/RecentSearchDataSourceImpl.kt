package com.example.map.googlemap.data.source.local

import com.example.map.googlemap.data.source.LocalSearchPlaceDataSource
import com.example.map.googlemap.data.source.vo.LocationVO
import com.example.map.googlemap.extensions.fromJson
import com.example.map.googlemap.extensions.toJsonString
import com.example.map.googlemap.utils.PrefUtils

class RecentSearchDataSourceImpl(private val prefUtils: PrefUtils) : LocalSearchPlaceDataSource {

    override fun saveLocationVOList(locationVO: LocationVO, callBack: () -> Unit) {
        var locationVOList = prefUtils.loadLocations().fromJson<List<LocationVO>>()
        locationVOList = if (!locationVOList.isNullOrEmpty()) {
            locationVOList.toMutableList().apply {
                val iter = iterator()
                while (iter.hasNext()) {
                    val nextLocationVO = iter.next()
                    if (locationVO == nextLocationVO) {
                        iter.remove()
                    }
                }
                add(locationVO)
            }
        } else {
            listOf(locationVO)
        }
        prefUtils.saveLocations(locationVOList.toJsonString())
        callBack()
    }

    override fun loadLocationVOList(): List<LocationVO>? {
        return prefUtils.loadLocations().fromJson<List<LocationVO>>()
    }

    override fun clearLocationVOList(callBack: () -> Unit) {
        prefUtils.saveLocations("")
        callBack()
    }
}