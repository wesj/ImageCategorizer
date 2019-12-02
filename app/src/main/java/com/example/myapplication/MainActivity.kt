package com.example.myapplication

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.TextureView
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseApp

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), PermissionManager.Callback, CaptureBuilder.Callback {
    private lateinit var viewFinder: TextureView
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    @RequiresApi(Build.VERSION_CODES.N)
    private var cameraManagement: CameraManagement? = null
    private var permissionManager: PermissionManager? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        FirebaseApp.initializeApp(baseContext)
        initializeCamera()
        initializeViews()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeViews() {
        viewFinder = findViewById(R.id.view_finder)
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            cameraManagement?.updateTransform(viewFinder)
        }

        var capture = findViewById<ImageButton>(R.id.fab)
        capture.setOnClickListener {
            cameraManagement?.capturePicture(externalMediaDirs.first())
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeCamera() {
        cameraManagement = CameraManagement(this)
        permissionManager = PermissionManager(REQUIRED_PERMISSIONS, this)
        permissionManager?.requestPermissions(this)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionManager?.onRequestPermissionsResult(baseContext, requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onGranted() {
        cameraManagement?.startCamera(this, viewFinder)
    }

    override fun onRefused() {
        Toast.makeText(baseContext, "You must grant camera permissions to use this app", Toast.LENGTH_LONG)
        finish()
    }

    override fun didSaveImage(file: File) {
        toolbar.post {
            Toast.makeText(baseContext, "Image saved " + file.absolutePath, Toast.LENGTH_SHORT)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "image/jpeg"

            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
            sharingIntent.putExtra(Intent.EXTRA_STREAM, photoURI)
            startActivity(Intent.createChooser(sharingIntent, "Share!"))
        }
    }

    override fun errorSavingImage(msg: String) {
        Toast.makeText(baseContext, "Error saving image: " + msg, Toast.LENGTH_LONG)
    }

}
