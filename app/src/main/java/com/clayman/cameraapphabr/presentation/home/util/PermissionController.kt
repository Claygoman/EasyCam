package com.clayman.cameraapphabr.presentation.home.util

import android.content.pm.PackageManager
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission

class PermissionController(
    private val activity: AppCompatActivity,
    private val requiredPermissions: Array<String>
) {

    init {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun requestPermissions() {
        val permissionsGranted = requiredPermissions.all { permission ->
            hasRequiredPermission(permission)
        }

        if (!permissionsGranted) {
            activity.requestPermissions(requiredPermissions, 1)
        }
    }

    private fun hasRequiredPermission(permission: String): Boolean {
        return checkSelfPermission(
            activity.applicationContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}