package com.example.atm

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
import com.example.atm.databinding.ActivityDepositBinding
import kotlinx.android.synthetic.main.activity_deposit.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class DepositActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    //var access:SharedPreferenceAccess?=null
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var binding: ActivityDepositBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_deposit)
        fun readDepositAmount() {
            val stringCreditAmount = enterDepositAmount.text.toString()
            if (stringCreditAmount.trim().isEmpty())
                enterDepositAmount.error = resources.getString(R.string.enter_amount)
            else {
                val creditAmount = Integer.parseInt(stringCreditAmount)
                if (creditAmount % 100 == 0) {
                    //val access=SharedPreferenceAccess(this)
                  // val mAccountNumber= SharedPreferenceAccess(this@DepositActivity).getSomeKey()
                    val sharedPreferences: SharedPreferences =
                        this.getSharedPreferences("account_number", Context.MODE_PRIVATE)
                    val mAccountNumber = sharedPreferences.getLong("valid accountNumber", 0L)
                    db = DetailsDatabase.getAppDataBase(this)
                    GlobalScope.launch {
                        val balance = db?.details()?.getAmount(mAccountNumber)
                        val creditAfterAmount = creditAmount + balance!!
                        db?.details()?.updateBalance(creditAfterAmount, mAccountNumber)
                        val date = Date()
                        val transactionDateFormat =
                            SimpleDateFormat("MMM dd yyy ", Locale.getDefault())
                       val transactionTimeFormat =
                           SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val transactionDate: String = transactionDateFormat.format(date)
                       val transactionTime: String = transactionTimeFormat.format(date)
                        val uniqueId = db?.transactionDetails()?.random()
                        db?.transactionDetails()?.insertTransactionDetails(
                            MiniStatementEntity(
                                uniqueId!!,
                                mAccountNumber,
                                transactionTime ,
                                transactionDate,
                                "credit",
                                creditAmount
                            )
                        )
                        // db?.details()?.addDate()


                        startActivity(
                            Intent(
                                this@DepositActivity,
                                BalanceActivity::class.java
                            )
                        )
                    }
                } else {
                    Toast.makeText(
                        this@DepositActivity,
                        resources.getString(R.string.enter_multiples_of_100),
                        Toast.LENGTH_LONG
                    )
                        .show()
                    enterDepositAmount.text?.clear()
                }

            }

        }

        buttonCredit.setOnClickListener {
            readDepositAmount()


        }
        enterDepositAmount.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if(keyEvent.action==KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    readDepositAmount()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@OnKeyListener true
                }
            }
            false
        })
        


        buttonBackToMainPage.setOnClickListener {
            val sharedPreferences: SharedPreferences =
                this@DepositActivity.getSharedPreferences("account_number", Context.MODE_PRIVATE)

            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val depositCancelIntent =
                Intent(this@DepositActivity, AccountNumberActivity::class.java)
            depositCancelIntent.addCategory(Intent.CATEGORY_HOME)
            depositCancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(depositCancelIntent)
            finish()
        }
    }

    override fun onBackPressed() {
    }


}

