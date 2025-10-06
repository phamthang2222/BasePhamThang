package com.phamthang.reverseaudio.ui.home.framgent_home

import com.phamthang.reverseaudio.base.BaseFragment
import com.phamthang.reverseaudio.base.CommonVM
import com.phamthang.reverseaudio.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<CommonVM, FragmentHomeBinding>() {
    override fun createViewModel(): Class<CommonVM> = CommonVM::class.java

    override fun getViewDataBinging(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override fun initView() {

    }

    override fun bindViewModel() {

    }

}