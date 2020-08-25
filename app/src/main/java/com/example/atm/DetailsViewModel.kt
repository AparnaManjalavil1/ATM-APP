//package com.example.atm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.activity_account_number.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/*class DetailsViewModel:ViewModel() {
     var db: DetailsDatabase? = null
        var listAccountNumber: List<AccountDetails>? = null
    init {
        readAccountNumber()
    }
    override fun onCleared() {
        super.onCleared()
    }
    fun readAccountNumber() {
        val stringAccountNumber = enterAccountNumber.text.toString()
        try {

            val accountNumber = stringAccountNumber.toLong()


            db = DetailsDatabase.getAppDataBase(this)
            GlobalScope.launch { listAccountNumber = db?.details()?.getDetails(accountNumber)
                if (listAccountNumber?.size!! > 0) {


                    val sharedPreferences: SharedPreferences =
                        this@AccountNumberActivity.getSharedPreferences(
                            sharedPreferenceAccountNumber, Context.MODE_PRIVATE
                        )
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putLong("valid accountNumber", accountNumber)
                    editor.apply()
                    val accountNumberIntent =
                        Intent(this@AccountNumberActivity, MainActivity::class.java)
                    startActivity(accountNumberIntent)


                } else {


                    this@AccountNumberActivity.runOnUiThread {
                        Toast.makeText(
                            this@AccountNumberActivity,
                            resources.getString(R.string.error_invalid_account_number),
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                    enterAccountNumber.text?.clear()

                }

            }

        } catch (ex: NumberFormatException) {
            if (stringAccountNumber.trim()
                    .isEmpty() || stringAccountNumber.trim().length < 10
            ) {
                enterAccountNumber.error =
                    resources.getString(R.string.error_invalid_account_number)
            }

        }
    }
}
 */