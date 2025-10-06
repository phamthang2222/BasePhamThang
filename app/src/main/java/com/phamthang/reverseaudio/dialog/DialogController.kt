package com.phamthang.reverseaudio.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.phamthang.reverseaudio.R
import com.phamthang.reverseaudio.base.BaseDialogFragment
import com.phamthang.reverseaudio.databinding.LayoutDialogControllerBinding
import com.phamthang.reverseaudio.extention.tap

class DialogController(
    private val type: TypeDialog,
    private val onPositive: () -> Unit,
) : BaseDialogFragment<LayoutDialogControllerBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutDialogControllerBinding = LayoutDialogControllerBinding.inflate(inflater, container, false)

    override fun initView() {


        binding.tvOke.tap {
            onPositive()
            dismiss()
        }

        binding.tvCancel.tap {
            dismiss()
        }
    }

    override fun bindView() {
        when (type) {
            TypeDialog.CLEAR_NOTE -> {
                updateView(
                    R.string.clear_notes,
                    R.string.content_clear_note,
                    R.string.clear,
                    R.string.cancel,
                    R.drawable.bg_red_r64,
                    R.drawable.bg_neutral_200_r64
                )
            }

            TypeDialog.RESTART_PUZZLE -> {
                updateView(
                    R.string.restart_puzzle,
                    R.string.content_restart_puzzle,
                    R.string.reset,
                    R.string.cancel,
                    R.drawable.bg_red_r64,
                    R.drawable.bg_neutral_200_r64
                )
            }

            TypeDialog.SOLVE_CURRENT_CELL -> {
                updateView(
                    R.string.solve_current_cell,
                    R.string.content_solve_current_cell,
                    R.string.OK,
                    R.string.cancel,
                    R.drawable.bg_blue_r64,
                    R.drawable.bg_neutral_200_r64
                )
            }

            TypeDialog.SOLVE_PUZZLE -> {
                updateView(
                    R.string.solve_puzzle,
                    R.string.content_solve_puzzle,
                    R.string.OK,
                    R.string.cancel,
                    R.drawable.bg_blue_r64,
                    R.drawable.bg_neutral_200_r64
                )
            }

            TypeDialog.RESTART_ALL -> {
                updateView(
                    R.string.restart_game,
                    R.string.content_restart_game,
                    R.string.reset,
                    R.string.cancel,
                    R.drawable.bg_red_r64,
                    R.drawable.bg_neutral_200_r64
                )
            }

            else -> {

            }
        }
    }

    private fun updateView(
        resTitle: Int,
        resContent: Int,
        resBtnPositive: Int,
        resBtnNegative: Int,
        bgBtnPositive: Int,
        bgBtnNegative: Int = R.drawable.bg_neutral_200_r64
    ) {
        binding.apply {
            tvTitle.text = getString(resTitle)
            tvContent.text = getString(resContent)
            tvOke.text = getString(resBtnPositive)
            tvCancel.text = getString(resBtnNegative)
            tvOke.setBackgroundResource(bgBtnPositive)
            tvCancel.setBackgroundResource(bgBtnNegative)
        }
    }

}


enum class TypeDialog {
    CLEAR_NOTE, RESTART_PUZZLE, SOLVE_CURRENT_CELL, SOLVE_PUZZLE, RESTART_ALL
}

