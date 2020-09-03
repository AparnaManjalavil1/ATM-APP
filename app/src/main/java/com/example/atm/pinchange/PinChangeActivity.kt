package com.example.atm.pinchange

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.R
import com.example.atm.accountdetails.AccountNumberActivity
import com.example.atm.databinding.ActivityPinchangeBinding
import com.example.atm.roomdatabase.DetailsDatabase
import com.example.atm.util.ConfigUtil
import com.example.atm.util.SharedPreferenceAccess
import kotlinx.android.synthetic.main.activity_pinchange.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PinChangeActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var binding: ActivityPinchangeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_pinchange
        )
        fun showDialog() {
            val builder = AlertDialog.Builder(this@PinChangeActivity)
            builder.setTitle(R.string.dialog_title)
            builder.setMessage(R.string.dialog_message)
            builder.setPositiveButton(resources.getString(R.string.dialog_ok)) { dialogInterface, which ->
                val pinChangeIntent =
                    Intent(
                        this@PinChangeActivity,
                        AccountNumberActivity::class.java
                    )
                ConfigUtil().intent(pinChangeIntent)
                startActivity(pinChangeIntent)
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        fun readNewPinNumber() {
            val stringPinChange = enterPinToChange.text.toString()
            val stringPinConfirmation = enterPinConfirmation.text.toString()
            try {
                if (stringPinChange.length == 4 && stringPinConfirmation.isNotEmpty()) {
                    val pinChangeNumber = Integer.parseInt(stringPinChange)
                    val pinConfirmationNumber = Integer.parseInt(stringPinConfirmation)
                    val mAccountNumber =
                        SharedPreferenceAccess(this).getInstanceObject(this).getPreference()
                    db = DetailsDatabase.getAppDataBase(this)
                    GlobalScope.launch {
                        val oldPassword = db?.details()?.getPassword(mAccountNumber)
                        if (pinChangeNumber != oldPassword!!) {
                            if (pinChangeNumber == pinConfirmationNumber) {
                                db?.details()?.changePassword(pinChangeNumber, mAccountNumber)
                                this@PinChangeActivity.runOnUiThread { showDialog() }

                            } else {
                                this@PinChangeActivity.runOnUiThread {
                                    ConfigUtil().toast(
                                        this@PinChangeActivity,
                                        resources.getString(R.string.error_pin_mismatch)
                                    )
                                    enterPinToChange.requestFocus()
                                    enterPinToChange.text?.clear()
                                    enterPinConfirmation.text?.clear()
                                }
                            }
                        } else {
                            this@PinChangeActivity.runOnUiThread {
                                ConfigUtil().toast(
                                    this@PinChangeActivity,
                                    resources.getString(R.string.error_already_exist)
                                )
                                enterPinToChange.requestFocus()
                                enterPinToChange.text?.clear()
                                enterPinConfirmation.text?.clear()
                            }
                        }
                    }
                } else if (stringPinChange.trim().isEmpty() && stringPinConfirmation.trim()
                        .isEmpty()
                ) {
                    enterPinToChange.requestFocus()
                    enterPinToChange.error = resources.getString(R.string.error_empty_pin_number)
                    enterPinConfirmation.error =
                        resources.getString(R.string.error_empty_pin_number)
                } else if (stringPinChange.length in 3 downTo 1) {
                    enterPinToChange.text?.clear()
                    enterPinConfirmation.text?.clear()
                    enterPinConfirmation.requestFocus()
                    enterPinConfirmation.error = resources.getString(R.string.error_invalid_length)
                } else if (stringPinConfirmation.trim().isEmpty()) {
                    enterPinConfirmation.requestFocus()
                    enterPinConfirmation.error =
                        resources.getString(R.string.error_empty_pin_number)
                } else if (stringPinChange.trim().isEmpty()) {
                    enterPinToChange.requestFocus()
                    enterPinToChange.error = resources.getString(R.string.error_empty_pin_number)
                }
            } catch (ex: NumberFormatException) {
                Log.i("NumberFormatException","Number format exception")
            }
        }
        buttonPinChange.setOnClickListener {
            readNewPinNumber()
        }
        enterPinConfirmation.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if (enterPinToChange.length() == 0 && enterPinConfirmation.length() != 0) {
                enterPinToChange.requestFocus()
                enterPinToChange.error = resources.getString(R.string.error_empty_pin_number)
                enterPinConfirmation.text?.clear()
            }
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    readNewPinNumber()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@OnKeyListener true
                }
            }
            false
        })
        buttonCancelPinChange.setOnClickListener {
            SharedPreferenceAccess(this).clearPreference()
            val pinChangeCancelIntent =
                Intent(this@PinChangeActivity, AccountNumberActivity::class.java)
            ConfigUtil().intent(pinChangeCancelIntent)
            startActivity(pinChangeCancelIntent)
            finish()
        }
    }

    override fun onBackPressed() {

    }

}
