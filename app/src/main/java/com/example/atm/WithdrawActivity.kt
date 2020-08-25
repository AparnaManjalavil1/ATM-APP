package com.example.atm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityWithdrawBinding
import kotlinx.android.synthetic.main.activity_withdraw.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class WithdrawActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    var access:SharedPreferenceAccess?=null
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var binding: ActivityWithdrawBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_withdraw)
        //val editDebitAmount = findViewById<EditText>(R.id.editDebitAmount)
        //val buttonWithdraw = findViewById<Button>(R.id.buttonDebit)
        fun readDebitAmount() {
            val stringDebitAmount = enterDebitAmount.text.toString()
            if (stringDebitAmount.trim().isEmpty())
                enterDebitAmount.error = resources.getString(R.string.enter_amount)
            else {
                val debitAmount = Integer.parseInt(stringDebitAmount)
                if (debitAmount % 100 == 0) {
                    val sharedPreferences: SharedPreferences =
                        this.getSharedPreferences("account_number", Context.MODE_PRIVATE)
                    val mAccountNumber = sharedPreferences.getLong("valid accountNumber", 0L)
                    db = DetailsDatabase.getAppDataBase(this)
                    GlobalScope.launch {
                        val balance = db?.details()?.getAmount(mAccountNumber)
                        val debitBalance: Int = balance!!
                        if (debitAmount <= debitBalance) {
                            val debitAfterAmount = debitBalance - debitAmount
                            db?.details()?.updateBalance(debitAfterAmount,mAccountNumber )
                            val date = Date()
                            val transactionDateFormat =
                                SimpleDateFormat("MMM dd yyy", Locale.getDefault())
                           val transactionTimeFormat =
                               SimpleDateFormat("hh:mm a", Locale.getDefault())
                            val transactionTime: String = transactionTimeFormat.format(date)
                            val transactionDate: String = transactionDateFormat.format(date)
                            val uniqueId = db?.transactionDetails()?.random()
                            db?.transactionDetails()?.insertTransactionDetails(
                                MiniStatementEntity(
                                    uniqueId!!,
                                    mAccountNumber,
                                    transactionTime,
                                    transactionDate,
                                    "debit",
                                    debitAmount
                                )
                            )
                            startActivity(
                                Intent(
                                    this@WithdrawActivity,
                                    BalanceActivity::class.java
                                )
                            )
                        } else {
                            this@WithdrawActivity.runOnUiThread {
                                Toast.makeText(
                                    this@WithdrawActivity,
                                    resources.getString(R.string.insufficient_balance),
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                enterDebitAmount.text?.clear()
                            }

                        }

                    }

                } else {
                    Toast.makeText(
                        this@WithdrawActivity,
                        resources.getString(R.string.enter_multiples_of_100),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                enterDebitAmount.text?.clear()
            }
        }
        binding.buttonDebit.setOnClickListener {
            readDebitAmount()


        }
        binding.enterDebitAmount.setOnKeyListener(View.OnKeyListener { view, keyCode,keyEvent ->
            if(keyEvent.action==KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    readDebitAmount()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@OnKeyListener true
                }
            }
            false
        })
        binding.buttonBackToMainPage.setOnClickListener {
            val sharedPreferences: SharedPreferences =
                this@WithdrawActivity.getSharedPreferences("account_number", Context.MODE_PRIVATE)

            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val withdrawCancelIntent =
                Intent(this@WithdrawActivity, AccountNumberActivity::class.java)
            withdrawCancelIntent.addCategory(Intent.CATEGORY_HOME)
            withdrawCancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(withdrawCancelIntent)
            finish()

        }
    }

    override fun onBackPressed() {
    }

}