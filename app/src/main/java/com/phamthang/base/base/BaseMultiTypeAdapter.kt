package com.phamthang.base.base

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseMultiTypeAdapter<M , VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {

    protected val listData = mutableListOf<M>()

    override fun getItemCount(): Int = listData.size

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    abstract override fun onBindViewHolder(holder: VH, position: Int)
    abstract override fun getItemViewType(position: Int): Int

    fun submitList(newList: List<M>) {

        listData.clear()
        listData.addAll(newList)
    }
}
