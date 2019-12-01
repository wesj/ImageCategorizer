package com.example.myapplication

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PermissionManagerUnitTest() {
    @Test
    fun testRequestPermissionsWhenAlreadyGranted() {
        val systemApi = mockk<PermissionManager.SystemAPI>()
        val callback = spyk<PermissionManager.Callback>()
        val activity = mockk<Activity>()
        val requestedPermissions = arrayOf("Foo", "Bar")
        val manager = PermissionManager(requestedPermissions,
                                        callback,
                                        systemApi)

        every { systemApi.checkSelfPermission(activity, "Foo") } returns true
        every { systemApi.checkSelfPermission(activity, "Bar") } returns true
        every { callback.onGranted() } returns Unit

        manager.requestPermissions(activity)
        verifySequence {
            systemApi.checkSelfPermission(activity, "Foo")
            systemApi.checkSelfPermission(activity, "Bar")
            callback.onGranted()
        }
    }

    @Test
    fun testRequestPermissionsWhenNotYetGranted() {
        val systemApi = mockk<PermissionManager.SystemAPI>()
        val callback = spyk<PermissionManager.Callback>()
        val activity = mockk<Activity>()
        val requestedPermissions = arrayOf("Foo", "Bar")
        val manager = PermissionManager(requestedPermissions,
            callback,
            systemApi)

        every { systemApi.checkSelfPermission(activity, "Foo") } returns false
        every { systemApi.requestPermissions(activity, requestedPermissions, manager.requestCode) } returns Unit

        manager.requestPermissions(activity)
        verifySequence {
            systemApi.checkSelfPermission(activity, "Foo")
            systemApi.requestPermissions(activity, requestedPermissions, manager.requestCode)
        }
    }

    @Test
    fun testPermissionsResultWhenAllGranted() {
        val systemApi = mockk<PermissionManager.SystemAPI>()
        val callback = spyk<PermissionManager.Callback>()
        val activity = mockk<Activity>()
        val requestedPermissions = arrayOf("Foo", "Bar")
        val manager = PermissionManager(requestedPermissions,
            callback,
            systemApi)

        every { systemApi.checkSelfPermission(activity, "Foo") } returns true
        every { systemApi.checkSelfPermission(activity, "Bar") } returns true
        every { callback.onGranted() } returns Unit

        manager.onRequestPermissionsResult(activity, manager.requestCode, requestedPermissions, IntArray(2, { 1 }))
        verifySequence {
            systemApi.checkSelfPermission(activity, "Foo")
            systemApi.checkSelfPermission(activity, "Bar")
            callback.onGranted()
        }
    }

    @Test
    fun testPermissionsResultWhenSomeGranted() {
        val systemApi = mockk<PermissionManager.SystemAPI>()
        val callback = spyk<PermissionManager.Callback>()
        val activity = mockk<Activity>()
        val manager = PermissionManager(arrayOf("Foo", "Bar"),
            callback,
            systemApi)

        every { systemApi.checkSelfPermission(activity, "Foo") } returns true
        every { systemApi.checkSelfPermission(activity, "Bar") } returns false
        every { callback.onRefused() } returns Unit

        manager.onRequestPermissionsResult(activity, manager.requestCode, arrayOf("Foo"), IntArray(2, { 1 }))
        verifySequence {
            systemApi.checkSelfPermission(activity, "Foo")
            systemApi.checkSelfPermission(activity, "Bar")
            callback.onRefused()
        }
    }

    @Test
    fun testPermissionsResultWithWrongRequestCode() {
        val systemApi = mockk<PermissionManager.SystemAPI>()
        val callback = spyk<PermissionManager.Callback>()
        val activity = mockk<Activity>()
        val manager = PermissionManager(arrayOf("Foo", "Bar"),
            callback,
            systemApi)

        manager.onRequestPermissionsResult(activity, 0, arrayOf("Foo"), IntArray(2, { PackageManager.PERMISSION_GRANTED }))
        verify {
            callback wasNot called
            systemApi wasNot called
        }
    }
}
