package com.example.map.googlemap.ui.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.map.googlemap.R
import com.example.map.googlemap.adapter.SearchPlaceAdapter
import com.example.map.googlemap.base.ui.BaseFullSheetDialogFragment
import com.example.map.googlemap.data.source.enums.SearchType
import com.example.map.googlemap.data.source.vo.LocationVO
import com.example.map.googlemap.databinding.SearchPlaceDialogBinding
import com.example.map.googlemap.extensions.showKeyboard
import com.example.map.googlemap.network.NetworkState
import com.example.map.googlemap.network.response.PlaceResponse
import com.example.map.googlemap.utils.REQUEST_SEARCH_TYPE
import com.example.map.googlemap.vm.SearchLocationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch
import com.example.map.googlemap.BR
import com.example.map.googlemap.base.ui.SimpleRecyclerView
import com.example.map.googlemap.databinding.RecentSearchPlaceItemBinding


class SearchPlaceDialog : BaseFullSheetDialogFragment<SearchPlaceDialogBinding>(R.layout.search_place_dialog) {

    var onPlaceClickListener: ((LocationVO) -> Unit)? = null
    private val searchLocationViewModel by viewModel<SearchLocationViewModel>()

    private val searchAdapter by lazy {
        SearchPlaceAdapter(
            onPlaceClickListener = {
                searchLocationViewModel.saveLocations(it)
                onPlaceClickListener?.invoke(it)
                dismiss()
            })
    }

    private val recentAdapter by lazy { //недавний адаптер
        object : SimpleRecyclerView.Adapter<LocationVO, RecentSearchPlaceItemBinding>(
                R.layout.recent_search_place_item,
                BR.location
            ) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int): SimpleRecyclerView.ViewHolder<RecentSearchPlaceItemBinding> {
                return super.onCreateViewHolder(parent, viewType).apply {
                    itemView.setOnClickListener {
                        val item = searchLocationViewModel.liveLocalLocations.value?.get(adapterPosition)
                        item?.let {
                            onPlaceClickListener?.invoke(item)
                        }
                       dismiss()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingElelments()
        placeStateLive()
        searchItemLive()
    }

    private fun bindingElelments(){
        binding.run {
            searchPlaceVM = searchLocationViewModel
            searchPlaceAdapter = searchAdapter
            recentSearchAdapter = recentAdapter

            when (arguments?.getSerializable(REQUEST_SEARCH_TYPE)) {
                SearchType.SOURCE -> tvToolbarTitle.text = getString(R.string.setting_departure)
                SearchType.DESTINATION -> tvToolbarTitle.text = getString(R.string.setting_destination)
            }

            rvContentSearch.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager(requireContext()).orientation
                )
            )

            rvContentRecent.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager(requireContext()).orientation
                )
            )

            rvContentRecent.post {
                searchLocationViewModel.loadLocalLocations()
            }

            etKeyword.setOnEditorActionListener { _, actionId, _ ->
                lifecycleScope.launch {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchLocationViewModel.onSearchClick()
                    }
                }
                true
            }

            etKeyword.doAfterTextChanged {
                lifecycleScope.launch {
                    searchLocationViewModel.onSearchClick()
                    etKeyword.showKeyboard()
                }
            }

            ivExit.setOnClickListener { dismiss() }
        }
    }

    private fun placeStateLive(){
        searchLocationViewModel.livePlaceState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkState.Init -> hideLoadingPopup()
                is NetworkState.Loading -> showLoadingPopup()
                is NetworkState.Success -> hideLoadingPopup()
                is NetworkState.Error -> {
                    hideLoadingPopup()
                }
            }
        })
    }

    private fun searchItemLive(){
        searchLocationViewModel.liveSearchItems.observe(viewLifecycleOwner, Observer {
            it?.observe(viewLifecycleOwner, Observer {
                searchAdapter.submitList(it)
            }) ?: run {
                searchAdapter.submitList(null)
            }
        })
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<PlaceResponse.ResultPlaceResponse>() {
            override fun areContentsTheSame(
                oldItem: PlaceResponse.ResultPlaceResponse,
                newItem: PlaceResponse.ResultPlaceResponse
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(
                oldItem: PlaceResponse.ResultPlaceResponse,
                newItem: PlaceResponse.ResultPlaceResponse
            ): Boolean = oldItem === newItem
        }

        fun getInstance(
            searchType: SearchType,
            onPlaceClickListener: ((LocationVO) -> Unit)?
        ) = SearchPlaceDialog().apply {
            arguments = Bundle().apply {
                putSerializable(REQUEST_SEARCH_TYPE, searchType)
            }
            this.onPlaceClickListener = onPlaceClickListener
        }
    }
}