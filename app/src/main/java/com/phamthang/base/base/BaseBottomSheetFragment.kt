package com.phamthang.base.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.phamthang.base.utils.SystemUtil
import com.phamthang.base.R
import com.phamthang.base.extention.logError


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

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? com.google.android.material.bottomsheet.BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)
            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    override fun getTheme(): Int {
        return R.style.BaseDialog
    }

    override fun onDetach() {
        dialog?.dismiss()
        dismiss()
        super.onDetach()
    }

    open fun safeDismiss() {
        if (isAdded || isVisible) {
            dismissAllowingStateLoss()
        }
    }

    open fun safeShow(manager: FragmentManager, tag: String? = null) {
        val tagName = tag ?: this::class.java.simpleName
        if (manager.isStateSaved || isAdded || isVisible) return
        try {
            show(manager, tagName)
        } catch (e: Exception) {
            logError(message = "safeShow: ${e.message}")
            e.printStackTrace()
        }
    }
}