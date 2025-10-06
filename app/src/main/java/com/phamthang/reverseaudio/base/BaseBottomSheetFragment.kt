package com.phamthang.reverseaudio.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phamthang.reverseaudio.R
import com.phamthang.reverseaudio.utils.SystemUtil


abstract class BaseBottomSheetFragment<VB : ViewBinding>(private val isCancel: Boolean = true) : BottomSheetDialogFragment() {

    lateinit var binding: VB

    protected abstract fun initView()
    protected abstract fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let { SystemUtil.setLocale(it) }
        binding = setViewBinding(inflater, container)
        isCancelable = isCancel
        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun getTheme(): Int {
        return R.style.BaseDialog
    }

    override fun onDetach() {
        dialog?.dismiss()
        dismiss()
        super.onDetach()
    }
}