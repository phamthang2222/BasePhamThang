package com.phamthang.reverseaudio.ui.home.fragment_statistic

import com.phamthang.reverseaudio.base.BaseFragment
import com.phamthang.reverseaudio.base.CommonVM
import com.phamthang.reverseaudio.databinding.FragmentSettingsBinding

class StatisticFragment : BaseFragment<CommonVM, FragmentSettingsBinding>() {
    override fun createViewModel(): Class<CommonVM> = CommonVM::class.java

    override fun getViewDataBinging(): FragmentSettingsBinding = FragmentSettingsBinding.inflate(layoutInflater)

    override fun initView() {

    }

    override fun bindViewModel() {

    }

}