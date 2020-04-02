package com.hoony.rssnewsreader.common

import android.content.Context
import android.widget.Toast

open class ToastPrinter {
    companion object {
        private var toast: Toast? = null
        fun show(context: Context, msg: String) {
            toast?.cancel()
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }
}