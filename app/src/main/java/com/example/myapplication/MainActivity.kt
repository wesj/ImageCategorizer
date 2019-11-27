package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.TextureView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewFinder: TextureView
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val cameraManagement = CameraManagement()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        viewFinder = findViewById(R.id.view_finder)
        if (allPermissionsGranted()) {
            viewFinder.post { cameraManagement.startCamera(this, viewFinder) }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            cameraManagement.updateTransform(viewFinder)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Companion.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { cameraManagement.startCamera(this, viewFinder) }
            } else {
                Toast.makeText(this,
                    "Don't have camera permission",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
