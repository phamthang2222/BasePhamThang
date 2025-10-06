package com.phamthang.reverseaudio.ui.home

import androidx.activity.enableEdgeToEdge
import androidx.navigation.fragment.NavHostFragment
import com.phamthang.reverseaudio.databinding.ActivityHomeBinding
import com.phamthang.reverseaudio.base.BaseActivity


class HomeActivity : BaseActivity<HomeVM, ActivityHomeBinding>() {

   
    override fun createViewModel(): Class<HomeVM> = HomeVM::class.java

    override fun getViewDataBinding(): ActivityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)

    override fun initView() {
        enableEdgeToEdge()
    }

    override fun bindViewModel() {
     
    }


}