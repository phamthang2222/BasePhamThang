package com.phamthang.base.ui.feature.home

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.navigation.fragment.NavHostFragment
import androidx.lifecycle.lifecycleScope
import com.phamthang.base.R
import com.phamthang.base.ads.AdsHelper
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.custome_view.BottomViewNav
import com.phamthang.base.databinding.ActivityHomeBinding
import com.phamthang.base.extention.clickVibrate
import com.phamthang.base.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        sharePrefApp.increaseCountRatingAppear()
        if (sharePrefApp.isFirstOpenApp) sharePrefApp.isFirstOpenApp = false

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
            viewModel.setNavigation(destination.id)
        }
    }

    /**
     * Quan sát UI State và UI Event theo chuẩn Flow.
     */
    override fun observeState() {
        // Observe UI State
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                updateBottomNavState(state.selectedNav)
                // Cập nhật các UI khác dựa trên state.wallpapers, state.isLoading, v.v.
            }
        }

        // Observe UI Event
        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is HomeEvent.ShowToast -> {
                        Toast.makeText(this@HomeActivity, event.message, Toast.LENGTH_SHORT).show()
                    }
                    HomeEvent.NavigateToSettings -> {
                        // Xử lý navigation event nếu cần
                    }
                }
            }
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
            navigateTo(R.id.homeFragment)
        }
        binding.statistic.clickVibrate {
            viewModel.setNavigation(R.id.statisticFragment)
            navigateTo(R.id.statisticFragment)
        }
        binding.settings.clickVibrate {
            viewModel.setNavigation(R.id.settingsFragment)
            navigateTo(R.id.settingsFragment)
        }
    }

    private fun navigateTo(destinationId: Int) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcvHome) as NavHostFragment
        val navController = navHostFragment.navController
        if (navController.currentDestination?.id != destinationId) {
            navController.navigate(destinationId)
        }
    }
}
