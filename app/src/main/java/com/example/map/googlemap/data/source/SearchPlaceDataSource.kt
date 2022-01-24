package com.example.map.googlemap.data.source

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.map.googlemap.network.NetworkState
import com.example.map.googlemap.network.response.PlaceResponse

class SearchPlaceDataSource(
    private val geocodeRepository: GeocodeRepository,
    private val keyword: String,
    private val livePlaceState: MutableLiveData<NetworkState<PlaceResponse>>
) : PageKeyedDataSource<String, PlaceResponse.ResultPlaceResponse>(
) {

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, PlaceResponse.ResultPlaceResponse>
    ) {
        keyword.let {
            geocodeRepository.getPlace(it)
                .doOnSubscribe { livePlaceState.postValue(NetworkState.loading()) }
                .doOnTerminate { livePlaceState.postValue(NetworkState.init()) }
                .subscribe({
                    callback.onResult(it.results, "", it.nextPageToken)
                    livePlaceState.postValue(NetworkState.success(it))
                }, {
                    livePlaceState.postValue(NetworkState.error(it))
                })
        }.let {

        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, PlaceResponse.ResultPlaceResponse>
    ) {
        keyword.let {
            geocodeRepository.getPlace(it)
                .doOnSubscribe { livePlaceState.postValue(NetworkState.loading()) }
                .doOnTerminate { livePlaceState.postValue(NetworkState.init()) }
                .subscribe({
                    callback.onResult(it.results, it.nextPageToken)
                    livePlaceState.postValue(NetworkState.success(it))
                }, {
                    livePlaceState.postValue(NetworkState.error(it))
                })
        }.let {
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, PlaceResponse.ResultPlaceResponse>
    ) { }
}