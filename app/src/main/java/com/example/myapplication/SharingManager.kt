package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class SharingManager {
    fun shareImage(file: File, context: Context) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/jpeg"

        val photoURI: Uri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        sharingIntent.putExtra(Intent.EXTRA_STREAM, photoURI)
        context.startActivity(Intent.createChooser(sharingIntent, "Share!"))
    }
}
