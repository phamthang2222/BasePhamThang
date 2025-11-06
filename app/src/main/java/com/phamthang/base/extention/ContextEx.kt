package com.phamthang.base.extention

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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

fun Context.copyToClipboard(text: String, view: View) {
    val clip = ClipData.newPlainText("", text)
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
}


@SuppressLint("MissingInflatedId", "InflateParams")
fun Context.customToast(messageRes: Int, icState: Int, anchorView: View) {
    val inflater = LayoutInflater.from(this)
    val layout: View = inflater.inflate(R.layout.toast_layout, null)

    layout.findViewById<TextView>(R.id.toastText).text = getString(messageRes)
    layout.findViewById<ImageView>(R.id.icState).setImageResource(icState)

    val location = IntArray(2)
    anchorView.getLocationOnScreen(location)

    val yOffsetDp = 40
    val yOffsetPx = (yOffsetDp * resources.displayMetrics.density).toInt()

    with(Toast(this)) {
        duration = Toast.LENGTH_SHORT
        view = layout
//        setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, yOffsetPx) // warp view
        setGravity(Gravity.FILL, 0, 0) // FULL SCREEN
        show()
    }
}

@SuppressLint("MissingInflatedId", "InflateParams")
fun Context.customToast(messageRes: Int, anchorView: View) {
    val inflater = LayoutInflater.from(this)
    val layout: View = inflater.inflate(R.layout.toast_layout, null)

    layout.findViewById<TextView>(R.id.toastText).text = getString(messageRes)
    layout.findViewById<ImageView>(R.id.icState).setImageResource(R.drawable.ic_tick)

    val location = IntArray(2)
    anchorView.getLocationOnScreen(location)

    val yOffsetDp = 40
    val yOffsetPx = (yOffsetDp * resources.displayMetrics.density).toInt()

    with(Toast(this)) {
        duration = Toast.LENGTH_SHORT
        view = layout
        setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, yOffsetPx)
        show()
    }
}

@SuppressLint("MissingInflatedId", "InflateParams")
fun Context.customToast(message: String, anchorView: View) {
    val inflater = LayoutInflater.from(this)
    val layout: View = inflater.inflate(R.layout.toast_layout, null)

    layout.findViewById<TextView>(R.id.toastText).text = message
    layout.findViewById<ImageView>(R.id.icState).setImageResource(R.drawable.ic_tick)

    val location = IntArray(2)
    anchorView.getLocationOnScreen(location)

    val yOffsetDp = 40
    val yOffsetPx = (yOffsetDp * resources.displayMetrics.density).toInt()

    with(Toast(this)) {
        duration = Toast.LENGTH_SHORT
        view = layout
        setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, yOffsetPx)
        show()
    }
}

fun Context.toastFail(anchorView: View){
   this.customToast(R.string.app_name,R.drawable.ic_tick,anchorView)
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
