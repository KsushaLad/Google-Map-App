package com.example.map.googlemap.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.example.map.googlemap.R
import com.example.map.googlemap.base.ui.BaseViewHolder
import com.example.map.googlemap.data.source.vo.LocationVO
import com.example.map.googlemap.databinding.SearchPlaceItemBinding
import com.example.map.googlemap.network.response.PlaceResponse
import com.example.map.googlemap.ui.dialog.SearchPlaceDialog

class SearchPlaceAdapter(private val onPlaceClickListener: ((LocationVO) -> Unit)? = null) :
    PagedListAdapter<PlaceResponse.ResultPlaceResponse, RecyclerView.ViewHolder>(SearchPlaceDialog.POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : BaseViewHolder<PlaceResponse.ResultPlaceResponse, SearchPlaceItemBinding>(R.layout.search_place_item, parent) {
            init {
                itemView.setOnClickListener {
                    val item = getItem(adapterPosition)
                    item?.geometry?.location?.let {
                        val locationVO = LocationVO(LatLng(it.lat, it.lng), item.formattedAddress, item.name)
                        onPlaceClickListener?.invoke(locationVO)
                    }
                }
            }

            override fun onViewCreated(item: PlaceResponse.ResultPlaceResponse?) {
                binding?.run {
                    tvName.text = item?.name
                    tvAddress.text = item?.formattedAddress
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? BaseViewHolder<*, *>)?.onBindViewHolder(getItem(position))
    }
}

