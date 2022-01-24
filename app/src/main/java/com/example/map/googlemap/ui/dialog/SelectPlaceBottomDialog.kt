package com.example.map.googlemap.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.example.map.googlemap.R
import com.example.map.googlemap.base.ui.BaseBottomSheetDialogFragment
import com.example.map.googlemap.databinding.SelectPlaceBottomSheetDialogBinding
import com.example.map.googlemap.network.NetworkState
import com.example.map.googlemap.vm.MapViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SelectPlaceBottomDialog :
    BaseBottomSheetDialogFragment<SelectPlaceBottomSheetDialogBinding>(R.layout.select_place_bottom_sheet_dialog) {

    private val mapViewModel by sharedViewModel<MapViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reverseGeocodeState()
    }

    private fun reverseGeocodeState(){
        binding.run {
            mapVM = mapViewModel
            mapViewModel.liveReverseGeocodeState.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is NetworkState.Init -> hideLoadingPopup()
                    is NetworkState.Loading -> showLoadingPopup()
                    is NetworkState.Success -> {
                        if (!it.item.results.isNullOrEmpty()) {
                            result = it.item.results[0]
                            mapViewModel.setPlaceType(it.item.results[0]?.types)
                        }
                    }
                }
            })
        }
    }

     fun searchLocation(latLng: LatLng) {
        mapViewModel.getLocationUseLatLng("${latLng.latitude},${latLng.longitude}")
    }
}