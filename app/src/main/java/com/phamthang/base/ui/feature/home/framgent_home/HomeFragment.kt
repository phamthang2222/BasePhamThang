package com.phamthang.base.ui.feature.home.framgent_home

import com.phamthang.base.base.BaseFragment
import com.phamthang.base.base.CommonVM
import com.phamthang.base.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<CommonVM, FragmentHomeBinding>() {
    override fun createViewModel(): Class<CommonVM> = CommonVM::class.java

    override fun getViewDataBinging(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override fun initView() {

    }

    override fun bindViewModel() {

    }

}