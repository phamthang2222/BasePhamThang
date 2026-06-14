package com.phamthang.base.ui.feature.home.framgent_home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.phamthang.base.base.BaseFragment
import com.phamthang.base.base.CommonVM
import com.phamthang.base.databinding.FragmentHomeBinding
import com.phamthang.base.dialog.DialogReward
import com.phamthang.base.extention.logDebug
import com.phamthang.base.extention.tap

class HomeFragment : BaseFragment<CommonVM, FragmentHomeBinding>() {
    override fun createViewModel(): Class<CommonVM> = CommonVM::class.java

    override fun getViewDataBinging(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

    override fun initView() {
        setupRewardDialogListener()
        binding.btnReward.tap {
            DialogReward.newInstance(type = 1).safeShow(childFragmentManager)
        }
    }

    override fun bindViewModel() {

    }

    private fun setupRewardDialogListener() {
        childFragmentManager.setFragmentResultListener(DialogReward.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val action = bundle.getString(DialogReward.KEY_ACTION_TYPE)
            when (action) {
                DialogReward.ACTION_WATCH -> {
                    logDebug(message = "ACTION_WATCH")
                }

                DialogReward.ACTION_WATCH_ALL -> {
                    logDebug(message = "ACTION_WATCH_ALL")
                }
            }
        }
    }

}