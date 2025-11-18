package com.phamthang.base.ui.defaut_ui.language.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.phamthang.base.extention.logDebug
import com.phamthang.base.extention.tap

import com.phamthang.base.databinding.ItemLanguageBinding
import com.phamthang.base.ui.defaut_ui.language.model.LanguageModel
import com.phamthang.base.ui.defaut_ui.language.model.LanguageSubModel

class LanguageAdapter(
    private val lists: List<LanguageModel>?,
    private val onClickSubLanguage: (LanguageSubModel) -> Unit,
    private val onClickDropDown: () -> Unit,
    private val onClickSingleLanguage : (LanguageModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LanguageViewHolder(ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    var languageSubAdapter: LanguageSubAdapter? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = lists!![position]
        if (holder is LanguageViewHolder) {
            holder.bind(data, position)
        }
    }

    override fun getItemCount(): Int {
        return lists?.size ?: 0
    }

    inner class LanguageViewHolder(private var binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LanguageModel, position: Int) {
            if (data.isExpand) {
                binding.imgDropdown.rotation = 180f
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.imgDropdown.rotation = 0f
                binding.recyclerView.visibility = View.GONE
            }
            data.image?.let { binding.imgAvatar.setImageResource(it) }
            binding.tvTitle.text = data.languageName
            binding.rbLanguage.isChecked = data.isCheck
            if(data.listLanguageSubModel.isNotEmpty()){
                binding.imgDropdown.isVisible = true
                binding.rbLanguage.isVisible = false
                languageSubAdapter = LanguageSubAdapter(data.listLanguageSubModel,
                    onClickSubLanguage = {
                        notifyItemChanged(findSelectedLanguageIndex())
                        it.isCheck = true
                        notifyItemChanged(position)
                        onClickSubLanguage.invoke(it)
                    }
                )
                binding.recyclerView.adapter = languageSubAdapter
            }else{
                binding.imgDropdown.isVisible = false
                binding.rbLanguage.isVisible = true
            }

            binding.root.tap {
                val isCurrentlyExpanded = data.isExpand
                if (data.listLanguageSubModel.isNotEmpty()) {
                    lists?.forEachIndexed { index, item ->
                        if (item.isExpand && index != position) {
                            item.isExpand = false
                            notifyItemChanged(index)
                        }
                    }
                    data.isExpand = !isCurrentlyExpanded
                    notifyItemChanged(position)
                    onClickDropDown.invoke()

                }else{
                    val prevIndex = findSelectedLanguageIndex()
                    logDebug(message = "prevIndex: $prevIndex")
                    if (prevIndex != -1) {
                        notifyItemChanged(prevIndex)
                    }
                    data.isCheck = !data.isCheck
                    notifyItemChanged(position)
                    onClickSingleLanguage(data)
                }
            }
            binding.rbLanguage.tap {
                binding.root.performClick()
            }
        }

    }

    fun findSelectedLanguageIndex(): Int {
        lists?.forEachIndexed { index, languageModel ->
            languageModel.listLanguageSubModel.forEach {
                if (it.isCheck) {
                    it.isCheck = false
                    return index
                }
            }
            if (languageModel.isCheck) {
                languageModel.isCheck = false
                return index
            }
        }
        return -1
    }

    fun checkSelectLanguage(): Boolean {
        lists?.forEach {
            it.listLanguageSubModel.forEach {
                if (it.isCheck) {
                    return true
                }
            }
        }
        return false
    }
}
