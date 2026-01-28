package com.phamthang.base.ui.default_ui.language.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phamthang.base.databinding.ItemLanguageSubBinding
import com.phamthang.base.ui.default_ui.language.model.LanguageSubModel


class LanguageSubAdapter(
    private val lists: List<LanguageSubModel>,
    private val onClickSubLanguage: (LanguageSubModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LanguageViewHolder(
            ItemLanguageSubBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = lists[position]
        if (holder is LanguageViewHolder) {
            holder.bind(data, position)
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    inner class LanguageViewHolder(private val binding: ItemLanguageSubBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LanguageSubModel, position: Int) {
            binding.ivFlag.setImageResource(data.flag)
            binding.tvLanguage.text = data.languageName
            binding.rbLanguage.isChecked = data.isCheck == true
            binding.root.setOnClickListener {
                onClickSubLanguage.invoke(data)
                notifyItemChanged(position)
            }
            binding.rbLanguage.setOnClickListener {
                onClickSubLanguage.invoke(data)
            }
            binding.rbLanguage.isChecked = data.isCheck
        }
    }

    fun setSelectLanguage(languageName: String) {
        lists.forEach {
            it.isCheck = it.languageName == languageName
        }
        notifyDataSetChanged()
    }

    fun checkSelectLanguage(): Boolean {
        lists.forEach {
            if (it.isCheck) {
                return true
            }
        }
        return false
    }
}
