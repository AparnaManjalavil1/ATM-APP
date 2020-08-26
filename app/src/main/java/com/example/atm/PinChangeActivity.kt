package com.example.atm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityPinchangeBinding
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
        binding=DataBindingUtil.setContentView(this,R.layout.activity_pinchange)
        //val editPinChange = findViewById<EditText>(R.id.editPinChange)
       // val editPinConfirmation = findViewById<EditText>(R.id.editPinConfirmation)
        fun showDialog() {
            val builder = AlertDialog.Builder(this@PinChangeActivity)
            builder.setTitle(R.string.dialog_title)
            builder.setMessage(R.string.dialog_message)
            builder.setPositiveButton("OK") { dialogInterface, which ->
                val pinChangeIntent =
                    Intent(
                        this@PinChangeActivity,
                        AccountNumberActivity::class.java
                    )
                pinChangeIntent.addCategory(Intent.CATEGORY_HOME)
                pinChangeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(pinChangeIntent)

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()

        }

        fun readNewPinNumber() {
            val stringPinChange = enterPinToChange.text.toString()
            val stringPinConfirmation = enterPinConfirmation.text.toString()
            if (stringPinChange.trim().isEmpty()) {
                enterPinToChange.error = getString(R.string.error_invalid_pin_number)
            } else if (stringPinConfirmation.trim().isEmpty()) {
                enterPinConfirmation.error = getString(R.string.error_confirmation_pin_number)
            }
            else if(enterPinToChange.length()!=4){
                enterPinToChange.error=getString(R.string.error_invalid_length)
                enterPinToChange.text?.clear()
                enterPinConfirmation.text?.clear()

            }
                else
             {
                val pinChangeNumber = Integer.parseInt(stringPinChange)
                val pinConfirmationNumber = Integer.parseInt(stringPinConfirmation)
                val sharedPreferences: SharedPreferences =
                    this.getSharedPreferences("account_number", Context.MODE_PRIVATE)
                val getAccountNumber = sharedPreferences.getLong("valid accountNumber", 0L)
                db = DetailsDatabase.getAppDataBase(this)
                GlobalScope.launch {
                    val oldPassword = db?.details()?.getPassword(getAccountNumber)
                    if (pinChangeNumber != oldPassword!!) {
                        if (pinChangeNumber == pinConfirmationNumber) {
                            db?.details()?.changePassword(pinChangeNumber, getAccountNumber)
                            this@PinChangeActivity.runOnUiThread { showDialog() }

                        } else {
                            this@PinChangeActivity.runOnUiThread {
                                Toast.makeText(
                                    this@PinChangeActivity,
                                    "Mismatch password",
                                    Toast.LENGTH_LONG
                                ).show()
                                enterPinToChange.text?.clear()
                                enterPinConfirmation.text?.clear()
                            }

                        }

                    } else {
                        this@PinChangeActivity.runOnUiThread {
                            Toast.makeText(
                                this@PinChangeActivity,
                                "Already exist",
                                Toast.LENGTH_LONG
                            ).show()
                            enterPinToChange.text?.clear()
                            enterPinConfirmation.text?.clear()
                        }
                    }

                }
            }

        }
        buttonPinChange.setOnClickListener {
            readNewPinNumber()


        }
        enterPinConfirmation.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if(keyEvent.action==KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    readNewPinNumber()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@OnKeyListener true
                }
            }
            false
        })
        buttonCancelPinChange.setOnClickListener {
            val sharedPreferences: SharedPreferences =
                this@PinChangeActivity.getSharedPreferences(
                    "account_number",
                    Context.MODE_PRIVATE
                )

            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val pinChangeCancelIntent =
                Intent(this@PinChangeActivity, AccountNumberActivity::class.java)
            pinChangeCancelIntent.addCategory(Intent.CATEGORY_HOME)
            pinChangeCancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(pinChangeCancelIntent)
            finish()
        }
    }


    override fun onBackPressed() {

    }

}
