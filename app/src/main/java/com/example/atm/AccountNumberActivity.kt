package com.example.atm


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityAccountNumberBinding
import kotlinx.android.synthetic.main.activity_account_number.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AccountNumberActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private var listAccountNumber: List<AccountDetails>? = null


    /**
     *note:Back CoroutineScope for implementing background thread
     * Job is used to start the coroutine
     */
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var binding: ActivityAccountNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_number)
        fun readAccountNumber() {
            val stringAccountNumber = enterAccountNumber.text.toString()
            try {

                val accountNumber = stringAccountNumber.toLong()
                db = DetailsDatabase.getAppDataBase(this)
                GlobalScope.launch {
                    listAccountNumber = db?.details()?.getDetails(accountNumber)
                    if (listAccountNumber?.size!! > 0) {
                        SharedPreferenceAccess(this@AccountNumberActivity).getInstanceObject(this@AccountNumberActivity)
                            .setPreference(accountNumber)
                        val accountNumberIntent =
                            Intent(this@AccountNumberActivity, MainActivity::class.java)
                        startActivity(accountNumberIntent)


                    } else {


                        this@AccountNumberActivity.runOnUiThread {
                            Toast.makeText(this@AccountNumberActivity, resources.getString(R.string.error_invalid_account_number), Toast.LENGTH_SHORT).show()
                            enterAccountNumber.text?.clear()


                        }


                    }

                }

            } catch (ex: NumberFormatException) {
                if (stringAccountNumber.trim()
                        .isEmpty()
                ) {
                    enterAccountNumber.requestFocus()
                    enterAccountNumber.error =
                        resources.getString(R.string.error_empty_account_number)
                }
                else if(stringAccountNumber.trim().length < 10){
                    enterAccountNumber.requestFocus()
                    enterAccountNumber.error =
                        resources.getString(R.string.error_invalid_account_number)
                }

            }
        }
        btnSubmit.setOnClickListener {
            readAccountNumber()


        }
        /**
         * InputMethodManager : Manage interactions by the client
         * inputmethod:bind the current input method
         */
        enterAccountNumber.setOnKeyListener(View.OnKeyListener { view, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                readAccountNumber()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                return@OnKeyListener true

            }
            false
        })

    }

    private var doubleBackToExitPressedOnce = false


    override fun onBackPressed() {
        SharedPreferenceAccess(this).clearPreference()

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
        }

        this.doubleBackToExitPressedOnce = true
        ToastAndIntent().toast(this, resources.getString(R.string.text_double_exit))
        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)


    }


}



