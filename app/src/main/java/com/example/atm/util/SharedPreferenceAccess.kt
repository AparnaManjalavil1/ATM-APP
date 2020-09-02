package com.example.atm.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceAccess() {
    private val sharedPreferenceAccountNumber: String = "accountNumber"
    private val setAccess: String = "valid_account_number"
    var sharedPreferences: SharedPreferences? = null
    private var access: SharedPreferenceAccess? = null
    private var sharedPreferenceAccountNumberToTransfer: String = ""
    private var setTransferAccountNumber = "valid_account_number_to_transfer"

    constructor(context: Context) : this() {
        sharedPreferences = context.applicationContext.getSharedPreferences(
            sharedPreferenceAccountNumber,
            Context.MODE_PRIVATE
        )

    }

    constructor(name: String, context: Context) : this() {
        this.sharedPreferenceAccountNumberToTransfer = name
        sharedPreferences = context.applicationContext.getSharedPreferences(
            this.sharedPreferenceAccountNumberToTransfer,
            Context.MODE_PRIVATE
        )

    }

    @Synchronized
    fun getInstanceObject(context: Context): SharedPreferenceAccess {
        if (access == null) {
            access = SharedPreferenceAccess(context)
        }
        return access as SharedPreferenceAccess
    }

    fun setPreference(accountNumber: Long) {
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        if (sharedPreferences!!.contains(sharedPreferenceAccountNumber))
            editor.putLong(setAccess, accountNumber)
        else (sharedPreferences!!.contains(sharedPreferenceAccountNumberToTransfer))
        editor.putLong(setTransferAccountNumber, accountNumber)
        editor.apply()
    }

    fun getPreference(): Long {
        return if (sharedPreferences!!.contains(sharedPreferenceAccountNumber))
            sharedPreferences!!.getLong(setAccess, 0L)
        else
            return sharedPreferences!!.getLong(setTransferAccountNumber, 0L)
    }

    fun clearPreference() {
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.clear()
        editor.apply()
    }

}