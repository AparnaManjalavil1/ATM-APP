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
import com.example.atm.databinding.ActivityPinNumberBinding
import kotlinx.android.synthetic.main.activity_pin_number.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PinNumberActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    var count:Int=0
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var binding: ActivityPinNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_pin_number)

        fun readPinNumber() {
            val stringPassword = enterPassword.text.toString()
            if (stringPassword.trim().isEmpty()) {
                enterPassword.error = resources.getString(R.string.error_invalid_pin_number)


            } else {


                val sharedPreferences: SharedPreferences =
                  this.getSharedPreferences("account_number", Context.MODE_PRIVATE)
                val mAccountNumber = sharedPreferences.getLong("valid accountNumber", 0L)
                db = DetailsDatabase.getAppDataBase(this)
                GlobalScope.launch {

                        val password = Integer.parseInt(stringPassword)
                        val passwordExist: Boolean? =
                            db?.details()?.isPasswordExist(mAccountNumber, password)
                        if (passwordExist!!) {
                            when (intent.getIntExtra("button_clicked", 0)) {
                                R.id.btnDeposit ->
                                    startActivity(
                                        Intent(
                                            this@PinNumberActivity,
                                            DepositActivity::class.java
                                        )
                                    )
                                R.id.btnWithdraw ->
                                    startActivity(
                                        Intent(
                                            this@PinNumberActivity,
                                            WithdrawActivity::class.java
                                        )
                                    )
                                R.id.btnTransfer ->
                                    startActivity(
                                        Intent(
                                            this@PinNumberActivity,
                                            TransferActivity::class.java
                                        )
                                    )
                                R.id.btnBalanceEnquirey -> {
                                    startActivity(
                                        Intent(
                                            this@PinNumberActivity,
                                            BalanceActivity::class.java
                                        )
                                    )

                                }
                                R.id.btnPinChange ->
                                    startActivity(
                                        Intent(
                                            this@PinNumberActivity,
                                            PinChangeActivity::class.java
                                        )
                                    )
                                R.id.btnMini ->
                                    startActivity(
                                        Intent(
                                            this@PinNumberActivity,
                                            MiniStatementActivity::class.java
                                        )
                                    )
                                R.id.btnOthers ->
                                    startActivity(
                                        Intent(
                                            this@PinNumberActivity,
                                            OthersActivity::class.java
                                        )
                                    )

                            }

                        }
                        else{
                            this@PinNumberActivity.count += 1


                                if (count < 3) {
                                    this@PinNumberActivity.runOnUiThread {
                                        Toast.makeText(
                                            this@PinNumberActivity,
                                            resources.getString(R.string.error_invalid_pin_number),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        enterPassword.text?.clear()
                                    }
                                }
                    else {


                        this@PinNumberActivity.runOnUiThread {
                            Toast.makeText(
                                this@PinNumberActivity,
                                resources.getString(R.string.error_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                            enterPassword.isEnabled=false
                            Handler(Looper.getMainLooper()).postDelayed({
                             enterPassword.isEnabled=true
                                val wrongPinCancelIntent=Intent(this@PinNumberActivity,AccountNumberActivity::class.java)
                                wrongPinCancelIntent.addCategory(Intent.CATEGORY_HOME)
                                wrongPinCancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(wrongPinCancelIntent)
                                finish()


                            },3000)




                        }


                    }

                    }

                }
            }

        }

        buttonSubmit.setOnClickListener {

            readPinNumber()

        }




        enterPassword.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if(keyEvent.action==KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    readPinNumber()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@OnKeyListener true
                }
            }
            false
        })

        btnCancel.setOnClickListener {
            val sharedPreferences: SharedPreferences =
                this@PinNumberActivity.getSharedPreferences("account_number",Context.MODE_PRIVATE)

            val editor= sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val pinCancelIntent = Intent(this@PinNumberActivity, AccountNumberActivity::class.java)
            pinCancelIntent.addCategory(Intent.CATEGORY_HOME)
            pinCancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(pinCancelIntent)
            finish()

        }
    }

    override fun onBackPressed() {

    }


        }



