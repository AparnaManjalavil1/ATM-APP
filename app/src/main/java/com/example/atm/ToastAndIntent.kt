package com.example.atm

import android.content.Context
import android.content.Intent
import android.widget.Toast

class ToastAndIntent {
    fun toast(context: Context, text: String) {
        Toast.makeText(context.applicationContext, text, Toast.LENGTH_LONG).show()
    }

    fun intent(intent: Intent) {
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

}