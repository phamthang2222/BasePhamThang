package com.phamthang.base.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.vifarmer.ads.lib.admob.Admob
import com.phamthang.base.R
import com.phamthang.base.base.BaseDialogFragment
import com.phamthang.base.databinding.DialogRewardBinding
import com.phamthang.base.extention.tap
import com.phamthang.base.utils.Constants

class DialogReward : BaseDialogFragment<DialogRewardBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogRewardBinding = DialogRewardBinding.inflate(inflater, container, false)


    override fun initView() {
        val type = arguments?.getInt(KEY_TYPE, 2) ?: 2

        if (Admob.getInstance().checkCondition(requireActivity(), Constants.RemoteKeys.rewarded_all)) {
            binding.llWatchAll.visibility = View.VISIBLE
        } else {
            binding.llWatchAll.visibility = View.GONE
        }

        binding.tvWatchAll.text = if (type == 1)
            getString(R.string.content_2_reward_1)
        else
            getString(R.string.content_2_reward_2)
    }

    override fun bindView() {
        binding.llWatch.tap {
            sendResult(ACTION_WATCH)
            dismissDialog()
        }

        binding.llWatchAll.tap {
            sendResult(ACTION_WATCH_ALL)
            dismissDialog()
        }
    }

    override val isDialogCancelable: Boolean = true

    private fun sendResult(action: String) {
        val result = Bundle().apply {
            putString(KEY_ACTION_TYPE, action)
        }
        setFragmentResult(REQUEST_KEY, result)
    }

    companion object {
        const val REQUEST_KEY = "DialogReward_RequestKey"
        const val KEY_ACTION_TYPE = "KEY_ACTION_TYPE"
        const val ACTION_WATCH = "ACTION_WATCH"
        const val ACTION_WATCH_ALL = "ACTION_WATCH_ALL"
        private const val KEY_TYPE = "KEY_TYPE"


        fun newInstance(type: Int = 2): DialogReward {
            return DialogReward().apply {
                arguments = bundleOf(KEY_TYPE to type)
            }
        }
    }
}