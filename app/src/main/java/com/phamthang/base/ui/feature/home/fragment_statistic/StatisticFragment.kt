package com.phamthang.base.ui.feature.home.fragment_statistic

import android.view.LayoutInflater
import android.view.ViewGroup
import com.phamthang.base.base.BaseFragment
import com.phamthang.base.base.CommonVM
import com.phamthang.base.databinding.FragmentSettingsBinding

class StatisticFragment : BaseFragment<CommonVM, FragmentSettingsBinding>() {
    override fun createViewModel(): Class<CommonVM> = CommonVM::class.java

    override fun getViewDataBinging(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

    override fun initView() {

    }

    override fun bindViewModel() {

    }

}