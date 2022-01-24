package com.example.map.googlemap.extensions

import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.map.googlemap.base.ui.SimpleRecyclerView


@BindingAdapter("replaceAll")
fun RecyclerView.replaceAll(list: List<Any>?) =
    (adapter as? SimpleRecyclerView.Adapter<Any, *>)?.replaceAll(list)


@BindingAdapter("adapter")
fun RecyclerView.bindRecyclerViewAdapter(adapter: SimpleRecyclerView.Adapter<*, out ViewDataBinding>) {
    this.adapter = adapter
}
