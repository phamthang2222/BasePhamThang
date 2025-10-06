package com.phamthang.reverseaudio.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<VB : ViewBinding, M>(
    private val inflater: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<BaseAdapter<VB, M>.BaseVH>() {

    protected val listData = mutableListOf<M>()
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        this.context = recyclerView.context

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH {
        val binding = inflater(LayoutInflater.from(parent.context), parent, false)
        return BaseVH(binding)
    }

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    fun submitList(newList: List<M>) {
        val diffResult = DiffUtil.calculateDiff(object : BaseDiffCallback<M>(listData, newList) {
            override fun areItemsTheSame(oldItem: M, newItem: M): Boolean {
                return this@BaseAdapter.areItemsTheSame(oldItem, newItem)
            }

            override fun areContentsTheSame(oldItem: M, newItem: M): Boolean {
                return this@BaseAdapter.areContentsTheSame(oldItem, newItem)
            }
        })
        listData.clear()
        listData.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setItemSelected(item: M){
        val position = listData.indexOf(item)
        if (position != RecyclerView.NO_POSITION) {
            notifyItemChanged(position)
        }
    }
    abstract fun areItemsTheSame(oldItem: M, newItem: M): Boolean

    abstract fun areContentsTheSame(oldItem: M, newItem: M): Boolean

    open fun onItemClickListener(item: M) {}

    abstract fun onBindData(binding: VB, item: M, position: Int)

    inner class BaseVH(internal val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClickListener(listData[pos])
                }
            }
        }

        fun bind(item: M) {
            onBindData(binding, item, adapterPosition)
        }
    }
}


