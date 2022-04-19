package com.msdev.ytidownloads

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class SnakeBar(private val view: View, private val context: Context) {

    fun success(message: String){

        val sb = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        sb.anchorView = view.findViewById(R.id.btnDownloadsFolder)
        val sbView = sb.view
        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_200))
        sb.show()
    }

    fun fail(message: String){

        val sb = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        sb.anchorView = view.findViewById(R.id.btnDownloadsFolder)
        val sbView = sb.view
        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        sb.show()
    }

    fun simple(message: String){

        val sb = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        sb.anchorView = view.findViewById(R.id.btnDownloadsFolder)
        sb.show()
    }
}