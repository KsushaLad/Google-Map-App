package com.example.map.googlemap.base.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class SimpleRecyclerView {

    abstract class Adapter<ITEM : Any, B : ViewDataBinding>(
        @LayoutRes private val layoutRes: Int,
        private val bindingVariableId: Int? = null
    ) : RecyclerView.Adapter<ViewHolder<B>>() {

        val items = mutableListOf<ITEM>()

        @SuppressLint("NotifyDataSetChanged")
        fun replaceAll(items: List<ITEM>?) {
            items?.let {
                this.items.run {
                    clear()
                    addAll(it)
                    notifyDataSetChanged()
                }
            }
        }

        fun removeAt(position: Int) {
            items.run {
                removeAt(position)
                notifyItemChanged(position)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : ViewHolder<B>(
                layoutRes = layoutRes,
                parent = parent,
                bindingVariableId = bindingVariableId
            ) {}

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
            holder.onBindViewHolder(items[position])
        }
    }

    abstract class ViewHolder<B : ViewDataBinding>(
        @LayoutRes layoutRes: Int,
        parent: ViewGroup?,
        private val bindingVariableId: Int?
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent?.context)
            .inflate(layoutRes, parent, false)
    ) {

        val binding: B = DataBindingUtil.bind(itemView)!!

        fun onBindViewHolder(item: Any?) {
            try {
                binding.run {
                    bindingVariableId?.let {
                        setVariable(it, item)
                    }
                    executePendingBindings()
                }
                itemView.visibility = View.VISIBLE
            } catch (e: Exception) {
                itemView.visibility = View.GONE
            }
        }
    }
}