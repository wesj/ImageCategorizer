package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class DefaultSystemApi: PermissionManager.SystemAPI {
    override fun checkSelfPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity, permissions, requestCode
        )
    }

}

class PermissionManager(
    private var requiredPermissions: Array<String>,
    private var callback: Callback,
    private var systemApi: SystemAPI = DefaultSystemApi()
) {
    val requestCode = 49239

    interface SystemAPI {
        fun checkSelfPermission(context: Context, permission: String): Boolean
        fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int)
    }

    interface Callback {
        fun onGranted()
        fun onRefused()
    }

    private fun allPermissionsGranted(context: Context) = requiredPermissions.all {
        systemApi.checkSelfPermission(context, it)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onRequestPermissionsResult(context: Context, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != this.requestCode) {
            return
        }

        // Its possible that the user granted some permissions a first time and some new ones now?
        // Lets ignore the permissions sent us here and just verify we have everything we need manually
        // That also lets us ignore grantResults here.
        // TODO: What are the performance impacts?

        if (allPermissionsGranted(context)) {
            callback.onGranted()
        } else {
            callback.onRefused()
        }
    }

    fun requestPermissions(activity: Activity) {
        if (allPermissionsGranted(activity)) {
            callback.onGranted()
        } else {
            systemApi.requestPermissions(activity, requiredPermissions, requestCode)
        }
    }
}
