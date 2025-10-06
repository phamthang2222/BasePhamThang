package com.phamthang.reverseaudio.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.phamthang.reverseaudio.extention.logDebug
import com.phamthang.reverseaudio.extention.isTiRaMiSu
import kotlin.collections.any
import kotlin.collections.filter
import kotlin.collections.isNotEmpty
import kotlin.collections.toTypedArray
import kotlin.let

object PermissionManager {

    val PERMISSION_IN_APP: Array<String> = buildList {
        add(Manifest.permission.CAMERA)
        add(Manifest.permission.POST_NOTIFICATIONS)
        add(Manifest.permission.RECORD_AUDIO)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    /**
     * Kiểm tra nếu tất cả các quyền đều được cấp.
     *
     * @return true nếu tất cả các quyền được cấp, ngược lại false.
     */
    fun grantedAllPermissions(context: Context?): Boolean {
        context?.let {
            for (permission in PERMISSION_IN_APP) {
                if (ContextCompat.checkSelfPermission(it, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Yêu cầu quyền từ người dùng.
     *
     * @param launcher ActivityResultLauncher để xử lý yêu cầu quyền.
     */
    fun requestAllPermissions(
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        launcher.launch(PERMISSION_IN_APP)
    }

    /**
     * Kiểm tra nếu người dùng đã từ chối quyền và có cần hiển thị lý do hay không.
     *
     * @return true nếu cần hiển thị lời giải thích, ngược lại false.
     */
    fun shouldShowRequestPermissionRationale(activity: Activity, permission1: String): Boolean {
        return PERMISSION_IN_APP.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }


    /**
     * Kiểm tra và yêu cầu cấp các quyền còn thiếu.
     *
     * @param launcher ActivityResultLauncher để xử lý yêu cầu quyền.
     */
    fun requestMissingPermissions(
        context: Context,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        val missingPermissions = PERMISSION_IN_APP.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            launcher.launch(missingPermissions.toTypedArray())
        }
    }

    /**
     *  check xem quyền đã được cấp chưa
     */
    fun hasPermission(per: String,context: Context): Boolean = ContextCompat.checkSelfPermission(context, per) == PackageManager.PERMISSION_GRANTED

    fun hasListPermission(listPer: List<String>, context: Context): Boolean {
        listPer.forEach {
            logDebug(message = "checkPer: $it: ${hasPermission(it, context)}")
        }
        return listPer.all { hasPermission(it, context) }
    }


    /**
     * Kiểm tra và yêu cầu cấp một quyền cụ thể nếu chưa được cấp.
     *
     * @param context Context để kiểm tra quyền.
     * @param permission Quyền cụ thể cần yêu cầu (ví dụ: Manifest.permission.CAMERA).
     * @param launcher ActivityResultLauncher để xử lý yêu cầu quyền.
     */
    fun requestAPermission(
        context: Context,
        permission: String,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(arrayOf(permission))
        }
    }
}
