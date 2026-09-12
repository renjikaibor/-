package com.aicodeassistant.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtils {

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    fun requestPermission(fragment: Fragment, permission: String, requestCode: Int) {
        fragment.requestPermissions(arrayOf(permission), requestCode)
    }

    fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    fun requestPermissions(fragment: Fragment, permissions: Array<String>, requestCode: Int) {
        fragment.requestPermissions(permissions, requestCode)
    }

    fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    fun shouldShowRationale(fragment: Fragment, permission: String): Boolean {
        return fragment.shouldShowRequestPermissionRationale(permission)
    }

    fun isPermissionPermanentlyDenied(activity: Activity, permission: String): Boolean {
        return !hasPermission(activity, permission) && !shouldShowRationale(activity, permission)
    }

    fun isPermissionPermanentlyDenied(fragment: Fragment, permission: String): Boolean {
        return !hasPermission(fragment.requireContext(), permission) && !shouldShowRationale(fragment, permission)
    }

    fun getDeniedPermissions(context: Context, permissions: Array<String>): List<String> {
        return permissions.filter { !hasPermission(context, it) }.toList()
    }

    fun getGrantedPermissions(context: Context, permissions: Array<String>): List<String> {
        return permissions.filter { hasPermission(context, it) }.toList()
    }

    // Common permission groups
    val CAMERA_PERMISSIONS = arrayOf("android.permission.CAMERA")
    val MICROPHONE_PERMISSIONS = arrayOf("android.permission.RECORD_AUDIO")
    val LOCATION_PERMISSIONS = arrayOf(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION"
    )
    val CONTACTS_PERMISSIONS = arrayOf(
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS"
    )
    val SMS_PERMISSIONS = arrayOf(
        "android.permission.SEND_SMS",
        "android.permission.READ_SMS"
    )
    val STORAGE_PERMISSIONS = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"
    )
    val NOTIFICATION_PERMISSIONS = arrayOf(
        "android.permission.POST_NOTIFICATIONS"
    )
    val MEDIA_PERMISSIONS = arrayOf(
        "android.permission.READ_MEDIA_IMAGES",
        "android.permission.READ_MEDIA_VIDEO",
        "android.permission.READ_MEDIA_AUDIO"
    )
}
