package com.phamthang.base.extention

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.phamthang.base.R
import com.phamthang.base.utils.PermissionManager
import java.util.Locale

fun Context.copyToClipboard(text: String, view: View) {
    val clip = ClipData.newPlainText("", text)
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
}

fun Context.shareText(text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type= "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, "Share"))
}

fun Context.vibrate(duration: Long = 50) {

    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    vibrator?.let {
        if (isOPlus()) {
            it.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            it.vibrate(duration)
        }
    }
}

fun Context.openAppPermissionSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
       data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

fun Context.hasPermission(perm: String): Boolean = ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED


fun Context.checkPermissionGrant(): Boolean{
   return PermissionManager.hasListPermission(PermissionManager.PERMISSION_IN_APP.toList(),this)
}

fun Context.getAppVersionName(): String {
    return packageManager.getPackageInfo(packageName, 0).versionName ?: "unknown"
}

fun Context.grantOverLayPer() : Boolean{
    return Settings.canDrawOverlays(this)
}

fun Context.grantOverlayPer() {
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:$packageName".toUri())
    startActivity(intent)
}

//----------------------------------------------------------------------------------
//in app
fun Context.vibrateFor100ms() {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    vibrator?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            it.vibrate(100)
        }
    }
}

fun Context.launchActivityIntent(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast("No valid app found")
    } catch (e: Exception) {
        toast(e.message.toString())
    }
}

fun Context.getLocalizedString(languageCode: String, resId: Int): String {
    val localeParts = languageCode.split("-")
    val myLocale = if (localeParts.size > 1) {
        Locale(localeParts[0], localeParts[1])
    } else {
        Locale(languageCode)
    }
    val config = Configuration(resources.configuration)
    config.setLocale(myLocale)
    val localizedContext = createConfigurationContext(config)
    return localizedContext.resources.getString(resId)
}
