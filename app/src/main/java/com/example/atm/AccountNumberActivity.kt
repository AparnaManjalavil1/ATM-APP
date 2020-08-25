package com.example.atm


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
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
    //private lateinit var viewModel: DetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_number)
        //viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        val configObject = ConfigProperties()
        val sharedPreferenceAccountNumber = configObject.getConfigValue(this, "sharedPreferenceName")

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
                            enterAccountNumber.text?.clear()


                        }


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
        val sharedPreferences: SharedPreferences =
            this@AccountNumberActivity.getSharedPreferences(
                "valid accountNumber ",
                Context.MODE_PRIVATE
            )

        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this@AccountNumberActivity,
            "Please click BACK again to exit ",
            Toast.LENGTH_SHORT
        ).show()


        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)


    }


}



