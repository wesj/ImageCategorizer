package com.example.myapplication

import android.content.Context
import android.widget.Toast

class ToastManager {
    fun showToast(context: Context, message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT)

    }
}
