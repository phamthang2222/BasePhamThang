package com.phamthang.base.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.phamthang.base.R
import com.phamthang.base.extention.isRPlus
import com.phamthang.base.extention.logError
import com.phamthang.base.utils.SystemUtil


abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException("Binding is only valid between onCreateView and onDestroyView")

    protected open val isDialogCancelable: Boolean = false

    protected open val hideSystemBars: Boolean = true

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    abstract fun initView()
    abstract fun bindView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setStyle(STYLE_NORMAL, R.style.BaseDialog)
        isCancelable = isDialogCancelable
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.BaseDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context?.let { SystemUtil.setLocale(it) }

        _binding = getViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindView()
    }

    override fun onStart() {
        super.onStart()
        if (hideSystemBars && isRPlus()) {
            dialog?.window?.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        // Xử lý độ rộng dialog nếu cần (thường dialog sẽ bị co lại)
//         dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    open fun dismissDialog() {
        if (isAdded || isVisible) {
            dismissAllowingStateLoss()
        }
    }
}