package com.example.atm

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity

class ToastAndIntent {
    fun toast(context:Context,text:String){
        Toast.makeText(context.applicationContext,text,Toast.LENGTH_LONG).show()
    }
    fun intent(intent: Intent){
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

}