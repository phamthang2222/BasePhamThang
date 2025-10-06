package com.phamthang.reverseaudio.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phamthang.reverseaudio.base.BaseDialogFragment
import com.phamthang.reverseaudio.databinding.DialogPermissionBinding
import com.phamthang.reverseaudio.extention.tap

import kotlin.apply

class DialogPer(
    private val onClickOke: () -> Unit
) : BaseDialogFragment<DialogPermissionBinding>(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogPermissionBinding = DialogPermissionBinding.inflate(inflater, container, false)

    override fun initView() {
        binding.apply {
            tvOke.tap {
                dismiss()
                onClickOke.invoke()
            }
        }

    }

    override fun bindView() {

    }


}