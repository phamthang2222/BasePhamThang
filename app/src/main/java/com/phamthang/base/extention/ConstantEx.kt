package com.phamthang.base.extention

import android.os.Build
import android.os.Environment
import android.os.Looper
import androidx.annotation.ChecksSdkIntAtLeast
import java.io.File


@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
fun isNougatMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1

//android 10
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

//android 12
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

//android 11
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

//android 8
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun isOPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

//android 14
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun isUpsideDownCake() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

//android 13
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isTiRaMiSu() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

//android 15
@ChecksSdkIntAtLeast(api = 35)
fun isAndroid15Plus() = Build.VERSION.SDK_INT >= 35

fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

//move call back to background thread
fun ensureBackgroundThread(callback: suspend () -> Unit) {
    if (isOnMainThread()) {
        launchIO {
            callback()
        }
    } else {
        launchIO{ callback()}
    }
}

const val CHILD_FOLDER_DOWNLOAD_THEME = "/VOICE_LOCK_SCREEN/theme/"
const val CHILD_FOLDER_DOWNLOAD_TEMPLATES = "/VOICE_LOCK_SCREEN/templates/"

val FOLDER_DOWNLOAD_THEME: File =
    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CHILD_FOLDER_DOWNLOAD_THEME)

val FOLDER_DOWNLOAD_TEMPLATES: File =
    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CHILD_FOLDER_DOWNLOAD_TEMPLATES)

