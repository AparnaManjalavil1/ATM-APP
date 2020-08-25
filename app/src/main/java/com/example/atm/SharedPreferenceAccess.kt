package com.example.atm

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceAccess {
    //val configObject=ConfigProperties()
   //val sharedPreferenceAccountNumber=configObject.getConfigValue(this@SharedPreferenceAccess , "sharedPreferenceName")
    val sharedPreferenceAccountNumber:String="accountNumber"
    val setAccess:String="valid_account_number"
   var sharedPreferences:SharedPreferences?=null
    var access:SharedPreferenceAccess?=null

    constructor(context: Context){
        sharedPreferences=context.applicationContext.getSharedPreferences(sharedPreferenceAccountNumber,Context.MODE_PRIVATE)
    }
    @Synchronized fun getInstanceObject(context:Context):SharedPreferenceAccess{
        if(access==null){
            access= SharedPreferenceAccess(context)
        }
        return access as SharedPreferenceAccess
    }
fun setSomeKey(accountNumber:Long){
    val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
    editor.putLong("valid accountNumber", accountNumber)
    editor.apply()
}
    fun getSomeKey(): Long {
        return sharedPreferences!!.getLong(setAccess,0L)
    }
}