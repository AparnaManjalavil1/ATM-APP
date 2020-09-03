package com.example.atm.others

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.*
import com.example.atm.accountdetails.AccountNumberActivity
import com.example.atm.databinding.ActivityOthersBinding
import com.example.atm.roomdatabase.DetailsDatabase
import com.example.atm.util.ConfigUtil
import com.example.atm.util.SharedPreferenceAccess
import kotlinx.android.synthetic.main.activity_others.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class OthersActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    private lateinit var binding: ActivityOthersBinding
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_others
        )
        fun showDialog() {
            val builder = AlertDialog.Builder(this@OthersActivity)
            builder.setTitle(R.string.dialog_title)
            builder.setMessage(R.string.dialog_message)
            builder.setPositiveButton(resources.getString(R.string.dialog_ok)) { _, _ ->
                val phoneNumberChangeIntent =
                    Intent(
                        this@OthersActivity,
                        AccountNumberActivity::class.java
                    )
                ConfigUtil().intent(phoneNumberChangeIntent)
                phoneNumberChangeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(phoneNumberChangeIntent)
                finish()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        fun readNewPhoneNumber() {
            val stringPhoneNumber = enterPhoneNumberChange.text.toString()
            if (stringPhoneNumber.trim()
                    .isEmpty()
            ) {
                enterPhoneNumberChange.requestFocus()
                enterPhoneNumberChange.error =
                    resources.getString(R.string.error_empty_phone_number)
            } else {
                val phoneNumber = stringPhoneNumber.toLong()
                val mAccountNumber =
                    SharedPreferenceAccess(this).getInstanceObject(this).getPreference()
                db =
                    DetailsDatabase.getAppDataBase(this)
                GlobalScope.launch {
                    if (stringPhoneNumber.trim().length == 10) {
                        if ((!(db?.details()
                                ?.isPhoneNumberExist(phoneNumber)!!))
                        ) {
                            db?.details()?.updatePhoneNumber(phoneNumber, mAccountNumber)
                            this@OthersActivity.runOnUiThread { showDialog() }
                        } else {
                            this@OthersActivity.runOnUiThread {
                                ConfigUtil().toast(
                                    this@OthersActivity,
                                    resources.getString(R.string.error_already_exist)
                                )
                                enterPhoneNumberChange.text?.clear()
                            }
                        }
                    } else {
                        this@OthersActivity.runOnUiThread {
                            ConfigUtil().toast(
                                this@OthersActivity,
                                resources.getString(R.string.error_invalid_phone_number)
                            )
                        }
                    }
                }
            }
        }
        buttonPhoneNumberChange.setOnClickListener {
            readNewPhoneNumber()
        }
        enterPhoneNumberChange.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    readNewPhoneNumber()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE)
                            as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@OnKeyListener true
                }
            }
            false

        })
        buttonCancelPhoneNumberChange.setOnClickListener {
            SharedPreferenceAccess(this).clearPreference()
            val phoneNumberChangeCancelIntent =
                Intent(this@OthersActivity, AccountNumberActivity::class.java)
            ConfigUtil().intent(phoneNumberChangeCancelIntent)
            startActivity(phoneNumberChangeCancelIntent)
            finish()
        }
    }

    override fun onBackPressed() {

    }
}