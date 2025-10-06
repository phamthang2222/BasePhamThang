package com.phamthang.reverseaudio.ui.language

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phamthang.reverseaudio.R
import com.phamthang.reverseaudio.databinding.ItemLanguageBinding


class LanguageAdapter(
    private val context: Context,
    private val lists: List<LanguageModel>,
    private val iClickLanguage: IClickLanguage,
    private var isLFO: Boolean,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isClicked = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LanguageViewHolder(ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = lists[position]
        if (holder is LanguageViewHolder) {
            holder.bind(data)
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    inner class LanguageViewHolder(val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LanguageModel) {
            data.image?.let { binding.imgAvatar.setImageResource(it) }
            binding.tvTitle.text = data.languageName
            binding.rbLanguage.isSelected = data.isCheck

            binding.relayEnglish.setOnClickListener { onItemClick(data) }
            binding.rbLanguage.setOnClickListener { onItemClick(data) }



            if (data.isCheck) {
                binding.relayEnglish.setBackgroundResource(R.drawable.bg_select_language)
                binding.tvTitle.setTextColor(context.getColor(R.color.white))
               /* if (isOPlus()) {
                    binding.tvTitle.typeface = context.resources.getFont(R.font.inter_24pt_semibold)
                }*/
                binding.rbLanguage.isSelected = true
            } else {
                binding.relayEnglish.setBackgroundResource(R.drawable.bg_unselect_language)
                binding.tvTitle.setTextColor(context.getColor(R.color.neutral_800))
//                if (isOPlus()) {
//                    binding.tvTitle.typeface = context.resources.getFont(R.font.lato_bold)
//                }
                binding.rbLanguage.isSelected = false
            }

        }

        private fun onItemClick(data: LanguageModel) {
            isClicked = true
            iClickLanguage.onClick(data)
            for (item in lists) {
                item.isCheck = item.languageName == data.languageName
            }
            notifyDataSetChanged()
        }
    }

}
