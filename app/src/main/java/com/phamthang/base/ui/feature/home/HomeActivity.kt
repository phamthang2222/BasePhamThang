package com.phamthang.base.ui.feature.home

import androidx.activity.enableEdgeToEdge
import androidx.navigation.fragment.NavHostFragment
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.custome_view.BottomViewNav
import com.phamthang.base.extention.clickVibrate
import com.phamthang.base.R
import com.phamthang.base.ads.AdsHelper
import com.phamthang.base.databinding.ActivityHomeBinding
import com.phamthang.base.utils.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeVM, ActivityHomeBinding>() {

    private val bottomNavItems: Map<Int, BottomViewNav> by lazy {
        mapOf(
            R.id.homeFragment to binding.home,
            R.id.statisticFragment to binding.statistic,
            R.id.settingsFragment to binding.settings
        )
    }

    override fun createViewModel(): Class<HomeVM> = HomeVM::class.java

    override fun getViewDataBinding(): ActivityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)

    override fun initView() {
        enableEdgeToEdge()
        onBottomClick()

        AdsHelper.loadCollapseBanner(
            this,
            this,
            Constants.AdsKey.collapse_banner,
            Constants.RemoteKeys.collapse_banner,
            binding.collapsibleBannerContainerView
        )
    }

    override fun bindViewModel() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcvHome) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomNavState(destination.id)
            viewModel.setNavigation(destination.id)
        }
        viewModel.selectedNav.observe(this) {
            navController.navigate(it)
        }
    }

    private fun updateBottomNavState(currentDestinationId: Int) {
        bottomNavItems.forEach { (destinationId, view) ->
            view.isActivated = (destinationId == currentDestinationId)
        }
    }

    private fun onBottomClick() {
        binding.home.clickVibrate {
            viewModel.setNavigation(R.id.homeFragment)
        }
        binding.statistic.clickVibrate {
            viewModel.setNavigation(R.id.statisticFragment)
        }
        binding.settings.clickVibrate {
            viewModel.setNavigation(R.id.settingsFragment)
        }

    }


}