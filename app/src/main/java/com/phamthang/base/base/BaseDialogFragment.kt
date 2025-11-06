package com.phamthang.base.base

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
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
import com.phamthang.base.utils.SystemUtil


abstract class BaseDialogFragment<VB : ViewBinding>(
    private val canBackToClose: Boolean = false,
    private val canCancelableOutside: Boolean = false
) : DialogFragment() {

    private var _binding: VB? = null
    val isBindingNull get() = _binding == null
    val binding get() = _binding!!

    lateinit var mActivity: Activity

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    abstract fun initView()
    abstract fun bindView()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = canBackToClose
    }

    override fun onStart() {
        super.onStart()
        if (isRPlus()) {
            dialog?.window?.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.BaseDialog)
        dialog.setCancelable(canCancelableOutside)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SystemUtil.setLocale(requireContext())
        _binding = getViewBinding(inflater, container)
        mActivity = requireActivity()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bindView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showDialog(manager: FragmentManager, tag: String = "") {
        if (!isAdded) {
            show(manager, tag)
        }
    }

    fun dismissDialog() {
        if (isAdded ) {
            dismiss()
        }
    }
}