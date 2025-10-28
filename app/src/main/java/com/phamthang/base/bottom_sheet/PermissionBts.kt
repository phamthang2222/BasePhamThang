package com.phamthang.base.bottom_sheet

import android.Manifest
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.phamthang.base.base.BaseBottomSheetFragment
import com.phamthang.base.dialog.DialogPer
import com.phamthang.base.extention.clickWithBounce
import com.phamthang.base.extention.gone
import com.phamthang.base.extention.grantOverLayPer
import com.phamthang.base.extention.grantOverlayPer
import com.phamthang.base.extention.isQPlus
import com.phamthang.base.extention.isTiRaMiSu
import com.phamthang.base.extention.openAppPermissionSettings
import com.phamthang.base.extention.visible
import com.phamthang.base.ui.home.HomeActivity
import com.phamthang.base.utils.PermissionManager
import com.phamthang.base.databinding.LayoutBtsPerBinding
import kotlin.apply
import kotlin.collections.all
import kotlin.collections.any
import kotlin.collections.first
import kotlin.collections.firstOrNull
import kotlin.collections.forEach
import kotlin.collections.isEmpty
import kotlin.collections.isNotEmpty
import kotlin.jvm.java

class PermissionBts(
    private val context: Context,
) : BaseBottomSheetFragment<LayoutBtsPerBinding>(true) {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val dialog by lazy {
        DialogPer() {
            AppOpenManager.getInstance().disableAppResumeWithActivity(HomeActivity::class.java)
            context.openAppPermissionSettings()
        }
    }

    override fun initView() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            handlePermissionsResult(permissions)
        }
        binding.swOverlay.setOnClickListener {
            if (binding.swOverlay.isChecked) {
                requireContext().grantOverlayPer()
                AppOpenManager.getInstance().disableAppResumeWithActivity(HomeActivity::class.java)
            }
        }
        binding.swMicro.setOnClickListener { if (binding.swMicro.isChecked) handlePermissionRequest(arrayOf(Manifest.permission.RECORD_AUDIO)) }
        binding.swCamera.setOnClickListener { if (binding.swCamera.isChecked) handlePermissionRequest(arrayOf(Manifest.permission.CAMERA)) }
        binding.swStorage.setOnClickListener { if (binding.swStorage.isChecked) handlePermissionRequest(getStoragePermissions()) }
        binding.swNoti.setOnClickListener { if (binding.swNoti.isChecked) handlePermissionRequest(getNotificationPermission()) }

        if (isTiRaMiSu()) binding.ctNoti.visible() else binding.ctNoti.gone()
        if (isQPlus()) binding.ctStorage.gone() else binding.ctStorage.visible()

        loadNative()

        binding.tvContinue.clickWithBounce { dismiss() }
    }

    override fun onResume() {
        super.onResume()

        updateSwitchStateForPermission(Manifest.permission.SYSTEM_ALERT_WINDOW, requireContext().grantOverLayPer())
        updateSwitchStateForPermission(
            Manifest.permission.RECORD_AUDIO,
            PermissionManager.hasPermission(Manifest.permission.RECORD_AUDIO, requireContext())
        )
        updateSwitchStateForPermission(Manifest.permission.CAMERA, PermissionManager.hasPermission(Manifest.permission.CAMERA, requireContext()))

        val storagePermissions = getStoragePermissions()
        if (storagePermissions.isNotEmpty()) {
            val allStorageGranted = storagePermissions.all { permission ->
                PermissionManager.hasPermission(permission, requireContext())
            }
            updateSwitchStateForPermission(storagePermissions.first(), allStorageGranted)
        }

        val notificationPermission = getNotificationPermission()
        if (notificationPermission.isNotEmpty()) {
            val notificationGranted = PermissionManager.hasPermission(
                notificationPermission.first(),
                requireContext()
            )
            updateSwitchStateForPermission(notificationPermission.first(), notificationGranted)
        }
    }

    override fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?): LayoutBtsPerBinding =
        LayoutBtsPerBinding.inflate(inflater, container, false)

    private fun updateSwitchStateForPermission(permission: String, isGranted: Boolean) {
        val switch = when (permission) {
            Manifest.permission.SYSTEM_ALERT_WINDOW -> binding.swOverlay
            Manifest.permission.RECORD_AUDIO -> binding.swMicro
            Manifest.permission.CAMERA -> binding.swCamera
            Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE -> binding.swStorage

            Manifest.permission.POST_NOTIFICATIONS -> binding.swNoti
            else -> null
        }
        switch?.apply {
            isChecked = isGranted
            isEnabled = !isGranted
        }
    }

    private fun handlePermissionRequest(permissionsToRequest: Array<String>) {
        if (permissionsToRequest.isEmpty()) return

        val permissionToShowRationale = permissionsToRequest.firstOrNull {
            PermissionManager.shouldShowRequestPermissionRationale(requireActivity(), it)
        }

        if (permissionToShowRationale != null) {
            dialog.showDialog(requireActivity().supportFragmentManager)
        } else {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    private fun getStoragePermissions(): Array<String> {
        return if (isTiRaMiSu()) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun getNotificationPermission(): Array<String> {
        return if (isTiRaMiSu()) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyArray()
        }
    }

    private fun handlePermissionsResult(permissions: Map<String, Boolean>) {
        val storagePermissions = getStoragePermissions()
        if (permissions.keys.any { it in storagePermissions }) {
            val allStorageGranted = storagePermissions.all { permissions[it] ?: false }
            updateSwitchStateForPermission(storagePermissions.first(), allStorageGranted)
            if (!allStorageGranted && storagePermissions.any { perm ->
                    !PermissionManager.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        perm
                    )
                }) {
                dialog.showDialog(requireActivity().supportFragmentManager)
            }
            return
        }

        permissions.entries.forEach { (permission, isGranted) ->
            updateSwitchStateForPermission(permission, isGranted)
            if (!isGranted && !PermissionManager.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                dialog.showDialog(requireActivity().supportFragmentManager)
            }
        }
    }

    private fun loadNative() {
        /*(activity as BaseActivity<*, *>).loadNative(
            binding.frAds,
            Constants.RemoteKeys.native_popup,
            Constants.RemoteKeys.native_popup,
            Constants.AdsKey.native_popup,
            Constants.AdsKey.native_popup,
            R.layout.native_bts_small_ads_with_button_bellow,
            R.layout.shimmer_native_small_with_button_bellow,
        )*/
    }
}
