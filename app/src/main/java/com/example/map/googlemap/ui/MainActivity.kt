package com.example.map.googlemap.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri 
import android.os.Bundle
import android.os.Looper
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.example.map.googlemap.R
import com.example.map.googlemap.base.ui.BaseActivity
import com.example.map.googlemap.data.source.enums.SearchType
import com.example.map.googlemap.data.source.vo.DirectionVO
import com.example.map.googlemap.databinding.MainActivityBinding
import com.example.map.googlemap.extensions.dip
import com.example.map.googlemap.extensions.enableTransparentStatusBar
import com.example.map.googlemap.network.NetworkState
import com.example.map.googlemap.network.response.Route
import com.example.map.googlemap.ui.dialog.SearchPlaceDialog
import com.example.map.googlemap.ui.dialog.SelectPlaceBottomDialog
import com.example.map.googlemap.utils.*
import com.example.map.googlemap.vm.MapViewModel
import com.google.android.gms.maps.*
import com.tedpark.tedpermission.rx2.TedRx2Permission
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.search_place_dialog.*
import kotlinx.android.synthetic.main.search_place_dialog.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

class MainActivity : BaseActivity<MainActivityBinding>(R.layout.main_activity),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMapLongClickListener, GoogleMap.OnPolylineClickListener {

    private lateinit var googleMap: GoogleMap
    private lateinit var locationCallback: LocationCallback
    private val fusedLocationProviderClient by lazy { FusedLocationProviderClient(this) }
    private val mapViewModel by viewModel<MapViewModel>()
    private val selectBottomDialog by lazy { SelectPlaceBottomDialog() }
    private var job: Job = Job()
    private var scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initMap()
        bindingObservers()
    }

    private fun bindingObservers() { //???????????????? ???? ????????????????????????
        binding.mapVM = mapViewModel
        mapViewModel.run {
            geocodeStateLive()
            startLocationVOLive()
            destinationLocationVOLive()
            directionStateLive()
            searchTypeLive()
        }
    }

    private fun cameraAtPoline(latLng: LatLng?){
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(ZOOM_POLINE).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun searchTypeLive() { //?????? ???????????? ?? ???????????????? ??????????????
        mapViewModel.liveSearchType.observe(this@MainActivity, Observer {
            it?.let {
                val searchType: SearchType = when (it) {
                    SearchType.SOURCE -> SearchType.SOURCE
                    SearchType.DESTINATION -> SearchType.DESTINATION
                }
                SearchPlaceDialog.getInstance(searchType,
                    onPlaceClickListener = { locationVO ->
                        when (searchType) {
                            SearchType.SOURCE -> mapViewModel.setDeparture(locationVO)
                            SearchType.DESTINATION -> mapViewModel.setDestination(locationVO)
                        }
                        addOneMarker(locationVO.latLng)
                        moveCamera(locationVO.latLng)
                    }).show(supportFragmentManager, "")
            }
        })
    }

    private fun directionStateLive() { //?????????????????????????? ???????? ?????????? ?????????? ??????????????
        mapViewModel.liveDirectionState.observe(this@MainActivity, Observer {
            if (!::googleMap.isInitialized) return@Observer
            scope.launch {
                when (it) {
                    is NetworkState.Init -> hideLoadingPopup() //?????????????? ???????????????? ???????????????????????? ????????
                    is NetworkState.Loading -> showLoadingPopup() //?????????????????? ???????????????? ???????????????????????? ????????
                    is NetworkState.Success -> {
                        val directionVO: MutableList<DirectionVO> = getDirectionsVO(it.item.routes)
                        mapViewModel.saveDirectionInfo(directionVO)
                        val polylines = directionVO.flatMap { it.latLng }
                        if (polylines.isNotEmpty()) {
                            scope.async {
                                drawOverViewPolyline(polylines)
                                addStartEndMarker(polylines[0], polylines[polylines.size - 1])
                                cameraAtPoline(polylines[(polylines.size - 1) / 2])
                            }
                        } else {
                            showToast(getString(R.string.toast_no_driving_route))
                        }

                    }
                }
            }
        })
    }

    private fun destinationLocationVOLive() { //?????????? ????????
        mapViewModel.liveDestinationLocationVO.observe(this@MainActivity, Observer {
            if (selectBottomDialog.isAdded) {
                selectBottomDialog.onCloseClick()
            }
            lifecycleScope.launch {
                showToast(getString(R.string.toast_complete_destination))
                mapViewModel.checkToReadyDriving(mapViewModel.liveStartLocationVO.value, mapViewModel.liveDestinationLocationVO.value)
            }

        })
    }

    private fun startLocationVOLive() { //???????????? ????????
        mapViewModel.liveStartLocationVO.observe(this@MainActivity, Observer {
            if (selectBottomDialog.isAdded) {
                selectBottomDialog.onCloseClick()
            }
            showToast(getString(R.string.toast_complete_departure))
            lifecycleScope.launch {
                mapViewModel.checkToReadyDriving(mapViewModel.liveStartLocationVO.value, mapViewModel.liveDestinationLocationVO.value)
            }

        })
    }

    private fun geocodeStateLive() { //?????????????????? ???????????????????????????? ?? ???????????????? ??????????????
        mapViewModel.liveGeocodeState.observe(this@MainActivity, Observer {
            when (it) {
                is NetworkState.Init -> hideLoadingPopup()
                is NetworkState.Loading -> showLoadingPopup()
                is NetworkState.Success -> it.item.toString()
            }
        })
    }

    private fun addStartEndMarker(departure: LatLng, destination: LatLng): Pair<Marker, Marker> { //???????????????????? ?????????????? ?? ???????????? ?? ??????????
        return googleMap.addMarker(MarkerOptions().position(departure)).apply {
            title = getString(R.string.departure)
            snippet = getString(R.string.marker_snippet_departure)
            setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        } to googleMap.addMarker(MarkerOptions().position(destination)).apply {
            title = getString(R.string.destination)
            snippet = getString(R.string.marker_snippet_destination)
            setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        }
    }

    private fun clearMap() { //???????????????? ??????????
        mapViewModel.liveAllArriveTime.value = ""
        mapViewModel.carPreviousLatLng = null
        if (::googleMap.isInitialized) {
            googleMap.clear()
        }
    }

    private fun drawOverViewPolyline(routes: List<LatLng>) { //?????????????????? ?????????? ???????????? ??????????
        googleMap.clear()
        googleMap.addPolyline(PolylineOptions().addAll(routes)).apply {
            color = ResourcesCompat.getColor(resources, R.color.black_87, theme)
            isClickable = true
        }
    }

    private fun getDirectionsVO(routes: List<Route?>?): MutableList<DirectionVO> { //???????????????????? ????????????????
        val directionVO: MutableList<DirectionVO> = mutableListOf()
        routes?.forEach {
            it?.legs?.forEach {
                mapViewModel.liveAllArriveTime.value = it?.duration?.text
                it?.steps?.forEachIndexed { idx, step ->
                    step?.let {
                        val latLngs = PolylineEncoding.decode(it.polyline.points)
                        directionVO.add(DirectionVO(latLngs, it.duration?.text, it.distance?.text, idx))
                    }
                }
            }
        }
        return directionVO
    }

    private fun checkPermissions() { //???????????????? ????????????????????
        compositeDisposable.add(TedRx2Permission.with(this)
            .setDeniedCloseButtonText(getString(R.string.cancel))
            .setGotoSettingButtonText(getString(R.string.setting))
            .setDeniedTitle(getString(R.string.request_gps_permission))
            .setDeniedMessage(getString(R.string.desc_gps_permission))
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .request()
            .subscribe({
                if (it.isGranted) {
                    setLocationListener()
                    getCurrentLocation()
                } else {
                    showToast(getString(R.string.desc_gps_permission))
                }
            }) {
            })
    }

    @SuppressLint("MissingPermission")
    private fun setLocationListener() { //???????????????????????????? ????????????????????????????
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) { //???? ???????????????????????????? ??????????????????
                super.onLocationResult(locationResult)
                mapViewModel.currLatLng.let { latLng ->
                    locationResult?.let { locationResult ->
                        for (location in locationResult.locations) {
                            if (latLng == null) {
                                mapViewModel.currLatLng = LatLng(location.latitude, location.longitude)
                                moveCamera(mapViewModel.currLatLng, ZOOM)
                                animateCamera(mapViewModel.currLatLng)
                                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                            }
                        }
                    }
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability?) { //???? ?????????????????????? ????????????????????????????
                super.onLocationAvailability(locationAvailability)
                mapViewModel.isAvailabilityLocation = locationAvailability?.isLocationAvailable ?: false
            }
        }

        val locationRequest = LocationRequest().setInterval(SET_INTERVAL).setFastestInterval(SET_FASTEST_INTERVAL).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun animateCamera(latLng: LatLng?) { //???????????????? ????????????
        latLng?.let {
            val cameraPosition = CameraPosition.Builder().target(it).zoom(ZOOM).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } ?: throw NullPointerException(getString(R.string.error_no_location))
    }

    private fun moveCamera(latLng: LatLng?, zoom: Float) { //?????????????????????? ????????????
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    private fun moveCamera(latLng: LatLng?) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(){ //?????????????? ????????????????????????????
        mapViewModel.run {
            googleMap.setOnMyLocationButtonClickListener(this@MainActivity)
            googleMap.setOnMyLocationClickListener(this@MainActivity)
            googleMap.setOnMapLongClickListener(this@MainActivity)
            googleMap.setOnPolylineClickListener(this@MainActivity)
            googleMap.isMyLocationEnabled = true
        }
    }

    private fun initView() { //???????????????????? StatusBar
       window.enableTransparentStatusBar()
    }

    private fun initMap() { //?????????????????????????? ??????????
        clearMap()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fcv_map) as SupportMapFragment
        mapFragment.getMapAsync(this@MainActivity)
    }

    override fun onMapReady(googleMap: GoogleMap?) { //???????????????????? ??????????
        googleMap?.let {
            this.googleMap = it
            googleMap.setPadding(dip(DIP_30), dip(DIP_70), dip(DIP_10), dip(DIP_70))
            checkPermissions()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(location: Location) {
    }

    override fun onMapLongClick(latLng: LatLng?) { //???????????? ???????? ???? ??????????
        if (mapViewModel.liveIsDrivingStarted.value == true) return
        latLng?.let {
            addOneMarker(it)
            selectBottomDialog.show(supportFragmentManager, selectBottomDialog.tag)
            selectBottomDialog.searchLocation(it)
        } ?: throw NullPointerException(getString(R.string.error_no_location))
    }

    private fun addOneMarker(latLng: LatLng) { //???????????????????? ???????????? ??????????????
        googleMap.addMarker(MarkerOptions().position(latLng).flat(true))
    }

    override fun onPolylineClick(polyline: Polyline?) { //?????????????? ?? GoogleMap
        val intent = Intent(Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?saddr=${mapViewModel.liveStartLocationVO.value?.addressName.toString()}&daddr=${mapViewModel.liveDestinationLocationVO.value?.addressName.toString()}"))
        startActivity(intent)
    }
}
