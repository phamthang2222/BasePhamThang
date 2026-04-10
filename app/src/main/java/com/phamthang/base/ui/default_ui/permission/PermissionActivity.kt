package com.phamthang.base.ui.default_ui.permission

import android.Manifest
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.amazic.library.Utils.EventTrackingHelper
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.phamthang.base.ads.AdsHelper
import com.phamthang.base.ads.NativeType
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.base.CommonVM
import com.phamthang.base.databinding.ActivityPermissionBinding
import com.phamthang.base.dialog.DialogPer
import com.phamthang.base.extention.gone
import com.phamthang.base.extention.isTiRaMiSu
import com.phamthang.base.extention.openAppPermissionSettings
import com.phamthang.base.extention.tap
import com.phamthang.base.extention.visible
import com.phamthang.base.ui.feature.home.HomeActivity
import com.phamthang.base.utils.Constants
import com.phamthang.base.utils.PermissionManager

class PermissionActivity : BaseActivity<CommonVM, ActivityPermissionBinding>() {

    private lateinit var perCameraLauncher: ActivityResultLauncher<String>
    private lateinit var perStorageLauncher: ActivityResultLauncher<String>

    override fun createViewModel(): Class<CommonVM> = CommonVM::class.java

    override fun getViewDataBinding(): ActivityPermissionBinding =
        ActivityPermissionBinding.inflate(layoutInflater)


    override fun initView() {
        EventTrackingHelper.logEvent(this, Constants.TrackingKeys.permission_open)

        AdsHelper.loadNative(
            this,
            this,
            frAds = binding.frAds,
            remoteKeyMain = Constants.RemoteKeys.native_per,
            remoteKeySecondary = Constants.RemoteKeys.native_per,
            type = NativeType.NATIVE_LARGE_ABOVE
        )

        perCameraLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            binding.swCamera.isChecked = isGranted
            binding.swCamera.isEnabled = !isGranted
            if (!isGranted && !PermissionManager.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showPermissionSettingsDialog()
            }
        }

        perStorageLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            binding.swStorage.isChecked = isGranted
            binding.swStorage.isEnabled = !isGranted
            if (!isGranted && !PermissionManager.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showPermissionSettingsDialog()
            }
        }

        updatePermissionSwitchStates()

        binding.swStorage.setOnCheckedChangeListener { button, isChecked ->
            if (button.isPressed && isChecked) {
                handlePermissionRequest(Manifest.permission.WRITE_EXTERNAL_STORAGE, perStorageLauncher)
                EventTrackingHelper.logEventWithAParam(this, Constants.TrackingKeys.permission_allow_click, "per_name", "STORAGE")
                AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
            } else if (button.isPressed && !isChecked) {
                binding.swStorage.isChecked = true
            }
        }

        binding.swCamera.setOnCheckedChangeListener { button, isChecked ->
            if (button.isPressed && isChecked) {
                handlePermissionRequest(Manifest.permission.CAMERA, perCameraLauncher)
                EventTrackingHelper.logEventWithAParam(this, Constants.TrackingKeys.permission_allow_click, "per_name", "CAMERA")
                AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
            } else if (button.isPressed && !isChecked) {
                binding.swCamera.isChecked = true
            }
        }

        binding.tvContinue.tap {
            EventTrackingHelper.logEvent(this, Constants.TrackingKeys.permission_continue_click)
            showActivity(HomeActivity::class.java, null)
            finishAffinity()
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            binding.rlWriteStorage.visible()
        }else{
            binding.rlWriteStorage.gone()
        }

        loadAds()
    }

    private fun handlePermissionRequest(permission: String, launcher: ActivityResultLauncher<String>) {
        when {
            PermissionManager.hasPermission(permission, this) -> {

            }
            PermissionManager.shouldShowRequestPermissionRationale(this, permission) -> {
                showPermissionSettingsDialog()
            }
            else -> {
                PermissionManager.requestAPermission(this, permission, launcher)
            }
        }
    }

    private fun showPermissionSettingsDialog() {
        val dialog = DialogPer {
            openAppPermissionSettings()
        }
        dialog.show(supportFragmentManager, "dialog_permission_settings")
    }

    override fun onResume() {
        super.onResume()
        updatePermissionSwitchStates()
        AppOpenManager.getInstance().enableAppResumeWithActivity(PermissionActivity::class.java)
    }

    private fun updatePermissionSwitchStates() {
        val isStorageGranted = PermissionManager.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)
        binding.swStorage.isChecked = isStorageGranted
        binding.swStorage.isEnabled = !isStorageGranted

        val isCameraGranted = PermissionManager.hasPermission(Manifest.permission.CAMERA, this)
        binding.swCamera.isChecked = isCameraGranted
        binding.swCamera.isEnabled = !isCameraGranted
    }

    override fun bindViewModel() {

    }

    private fun loadAds(){
        AdsHelper.loadNative(
            activity = this,
            lifecycleOwner = this,
            frAds = binding.frAds,
            remoteKeyMain = Constants.RemoteKeys.native_per,
            type = NativeType.NATIVE_LARGE_ABOVE
        )
    }

}