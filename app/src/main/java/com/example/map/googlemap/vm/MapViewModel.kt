package com.example.map.googlemap.vm

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import androidx.lifecycle.ViewModel
import com.example.map.googlemap.data.source.DirectionRepository
import com.example.map.googlemap.data.source.GeocodeRepository
import com.example.map.googlemap.data.source.enums.SearchType
import com.example.map.googlemap.data.source.vo.DirectionVO
import com.example.map.googlemap.data.source.vo.LocationVO
import com.example.map.googlemap.extensions.nonNull
import com.example.map.googlemap.extensions.toParam
import com.example.map.googlemap.network.NetworkState
import com.example.map.googlemap.network.response.DirectionResponse
import com.example.map.googlemap.network.response.GeocodeResponse
import com.example.map.googlemap.network.response.ReverseGeocodeResponse
import com.example.map.googlemap.network.response.ReverseGeocodeResponse.Result.Geometry.Location


class MapViewModel(private val geocodeRepository: GeocodeRepository, private val directionRepository: DirectionRepository
) :ViewModel() {

    private val _liveSelectPlaceType = MutableLiveData<String>()

    val liveSelectPlaceType: LiveData<String> get() = _liveSelectPlaceType //выбор тип выбранного места

    val liveAllArriveTime = MutableLiveData<String>() //время прибытия

    var isAvailabilityLocation = false //доступность местоположения

    var carPreviousLatLng: LatLng? = null

    var currLatLng: LatLng? = null

    private var _liveSearchType = MutableLiveData<SearchType>()
    val liveSearchType: LiveData<SearchType> get() = _liveSearchType //тип поиска

    private var _liveIsDrivingPossible = MutableLiveData<Boolean>().apply { value = false }
    val liveIsDrivingStarted get() = _liveIsDrivingPossible

    private var _liveDirectionVO = MutableLiveData<List<DirectionVO>>()

    private var _liveIsEnabledDriving = MutableLiveData<Boolean>().nonNull().apply { value = false }
    val liveIsEnabledDriving: LiveData<Boolean> get() = _liveIsEnabledDriving

    private var _liveStartLocationVO = MutableLiveData<LocationVO>()
    val liveStartLocationVO: LiveData<LocationVO> get() = _liveStartLocationVO

    private var _liveDestinationLocationVO = MutableLiveData<LocationVO>()
    val liveDestinationLocationVO: LiveData<LocationVO> get() = _liveDestinationLocationVO

    private val _liveGeocodeState =
        MutableLiveData<NetworkState<GeocodeResponse>>().apply { value = NetworkState.init() }
    val liveGeocodeState: LiveData<NetworkState<GeocodeResponse>> get() = _liveGeocodeState //состояние геокодирования

    private val _liveReverseGeocodeState =
        MutableLiveData<NetworkState<ReverseGeocodeResponse>>().apply {
            value = NetworkState.init()
        }
    val liveReverseGeocodeState: LiveData<NetworkState<ReverseGeocodeResponse>> get() = _liveReverseGeocodeState //состояние обратного геокодирования

    private val _liveDirectionState =
        MutableLiveData<NetworkState<DirectionResponse>>().apply { value = NetworkState.init() }
    val liveDirectionState: LiveData<NetworkState<DirectionResponse>> get() = _liveDirectionState //состояние направления


   @SuppressLint("CheckResult")
   fun getLocationUseLatLng(latLng: String) { //получение местоположения используя долготу и широту
            geocodeRepository.getLocationUseLatLng(latLng)
                .doOnSubscribe { _liveReverseGeocodeState.value = NetworkState.loading() }
                .doOnTerminate { _liveReverseGeocodeState.value = NetworkState.init() }
                .subscribe({
                    _liveReverseGeocodeState.value = NetworkState.success(it)
                }, {
                    _liveReverseGeocodeState.value = NetworkState.error(it)
                })
    }

    fun setDeparture(location: Location?, addressName: String?) { //отправление
        location?.let {
            _liveStartLocationVO.value = LocationVO(LatLng(it.lat, it.lng), addressName)
        }
    }

    fun setDeparture(locationVO: LocationVO?) { //отправление
        locationVO?.let {
            _liveStartLocationVO.value = it
        }
    }

    fun setDestination(location: Location?, addressName: String?) { //установка пункта назначения
        location?.let {
            _liveDestinationLocationVO.value = LocationVO(LatLng(it.lat, it.lng), addressName)
        }
    }

    fun setDestination(locationVO: LocationVO?) { //установка пункта назначения
        locationVO?.let {
            _liveDestinationLocationVO.value = locationVO
        }
    }

    fun checkToReadyDriving( //проверка готовности
        departureLocationVO: LocationVO?,
        destinationLocationVO: LocationVO?
    ) {
        _liveIsEnabledDriving.value = departureLocationVO != null && destinationLocationVO != null
    }

    @SuppressLint("CheckResult")
    fun startDriving() { //начало поездки
            directionRepository.getDriveCourse(
            liveStartLocationVO.value?.latLng?.toParam().toString(),
            liveDestinationLocationVO.value?.latLng?.toParam().toString()
        ).doOnSubscribe { _liveDirectionState.value = NetworkState.loading() }
            .doOnTerminate { _liveDirectionState.value = NetworkState.init() }
            .subscribe({
                _liveDirectionState.value = NetworkState.success(it)
            }, {
                _liveDirectionState.value = NetworkState.error(it)
            })
    }

    fun saveDirectionInfo(directionsVO: List<DirectionVO>) { //сохранение информации о направлении
        _liveDirectionVO.value = directionsVO
    }

    fun onSearchClick(searchType: SearchType) { //нажатие на поиск
        _liveSearchType.value = searchType
    }

    fun setPlaceType(placeType: List<String?>?) { //задание типа места
        val place = StringBuilder()
        placeType?.forEach {
            place.append(it)
        }
        _liveSelectPlaceType.value = place.toString()
    }
}