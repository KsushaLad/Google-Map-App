package com.example.map.googlemap.data.source

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.map.googlemap.network.NetworkState
import com.example.map.googlemap.network.response.PlaceResponse

class SearchPlaceDataSourceFactory(
    private val geocodeRepository: GeocodeRepository,
    private val keyword: String,
    private val livePlaceState: MutableLiveData<NetworkState<PlaceResponse>>
    ) : DataSource.Factory<String, PlaceResponse.ResultPlaceResponse>() {

    override fun create(): DataSource<String, PlaceResponse.ResultPlaceResponse> {
        return SearchPlaceDataSource(geocodeRepository, keyword, livePlaceState)
    }
}
