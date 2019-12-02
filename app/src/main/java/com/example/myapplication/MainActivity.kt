package com.example.myapplication

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.TextureView
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity(), PermissionManager.Callback, CaptureBuilder.Callback,
    AnalyzerBuilder.Callback {
    private lateinit var viewFinder: TextureView
    private var executor = Executors.newSingleThreadExecutor()
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    @RequiresApi(Build.VERSION_CODES.N)
    private var cameraManagement: CameraManagement? = null
    private var permissionManager: PermissionManager? = null
    private val captureManager: CaptureBuilder = CaptureBuilder(this)
    private val previewManager: PreviewBuilder = PreviewBuilder()
    private val analyzerManager: AnalyzerBuilder = AnalyzerBuilder(this)
    private var toastManager: ToastManager? = null
    private var sharingManager: SharingManager? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        FirebaseApp.initializeApp(baseContext)
        initializeViews()
        initializeCamera()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeViews() {
        viewFinder = findViewById(R.id.view_finder)
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            cameraManagement?.updateTransform(viewFinder)
        }

        var capture = findViewById<ImageButton>(R.id.fab)
        capture.setOnClickListener {
            captureManager.capturePicture(externalMediaDirs.first(), executor)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeCamera() {
        cameraManagement = CameraManagement(captureManager,
                                            previewManager,
                                            analyzerManager,
                                            executor)
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
        toolbar.post {
          toastManager?.showToast(baseContext, "You must grant camera permissions to use this app")
          finish()
        }
    }

    override fun didSaveImage(file: File) {
        toolbar.post {
            toastManager?.showToast(baseContext, "Image saved " + file.absolutePath)
            sharingManager?.shareImage(file, this)
        }
    }

    override fun errorSavingImage(msg: String) {
        toolbar.post {
            Toast.makeText(baseContext, resources.getString(R.string.error_saving_image, msg), Toast.LENGTH_LONG)
        }
    }

    override fun onItemFound(item: AnalyzerBuilder.Type) {
        if (item == AnalyzerBuilder.Type.Unknown) {
            toolbar.setTitle(R.string.unknown_item)
        } else {
            toolbar.title = resources.getString(R.string.found_item, item.name)
        }
    }

    override fun nothingFound() {
        toolbar.setTitle(R.string.nothing_found)
    }

}
