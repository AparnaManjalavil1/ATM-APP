package com.example.atm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityDepositOrWithdrawBinding
import kotlinx.android.synthetic.main.activity_deposit_or_withdraw.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DepositOrWithdrawActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityDepositOrWithdrawBinding
    private var db: DetailsDatabase? = null
    private lateinit var job: Job


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deposit_or_withdraw)

        val showDeposit = intent?.getIntExtra("Deposit", 0)
        val showWithdraw = intent?.getIntExtra("withdraw", 0)
        fun readAmount() {
            val stringAmount = enterAmount.text.toString()
            if (stringAmount.trim().isEmpty()) {
                enterAmount.requestFocus()
                enterAmount.error = resources.getString(R.string.enter_amount)
            } else {
                val amount = Integer.parseInt(stringAmount)
                if (amount % 100 == 0) {

                    val mAccountNumber =
                        SharedPreferenceAccess(this@DepositOrWithdrawActivity).getInstanceObject(
                            this@DepositOrWithdrawActivity
                        )
                            .getPreference()

                    db = DetailsDatabase.getAppDataBase(this)
                    GlobalScope.launch {
                        val balance = db?.details()?.getAmount(mAccountNumber)
                        val transactionDate = MiniStatementTable().setTransactionDate()
                        val transactionTime = MiniStatementTable().setTransactionTime()
                        val uniqueId = db?.transactionDetails()?.random()
                        if (showDeposit == 1) {

                            val creditAfterAmount = amount + balance!!
                            db?.details()?.updateBalance(creditAfterAmount, mAccountNumber)
                            db?.transactionDetails()?.insertTransactionDetails(
                                MiniStatementEntity(
                                    uniqueId!!,
                                    mAccountNumber,
                                    transactionTime,
                                    transactionDate,
                                    "credit",
                                    amount
                                )
                            )
                            startActivity(
                                Intent(
                                    this@DepositOrWithdrawActivity,
                                    BalanceActivity::class.java
                                )
                            )

                        } else if (showWithdraw == 2) {
                            if (amount <= balance!!) {
                                val debitAfterAmount = balance - amount
                                db?.details()?.updateBalance(debitAfterAmount, mAccountNumber)
                                db?.transactionDetails()?.insertTransactionDetails(
                                    MiniStatementEntity(
                                        uniqueId!!,
                                        mAccountNumber,
                                        transactionTime,
                                        transactionDate,
                                        "debit",
                                        amount
                                    )
                                )
                                startActivity(
                                    Intent(
                                        this@DepositOrWithdrawActivity,
                                        BalanceActivity::class.java
                                    )
                                )
                            } else {
                                this@DepositOrWithdrawActivity.runOnUiThread {
                                    ToastAndIntent().toast(
                                        this@DepositOrWithdrawActivity,
                                        resources.getString(R.string.insufficient_balance)
                                    )
                                    enterAmount.text?.clear()
                                }


                            }
                        }
                    }
                } else {
                    ToastAndIntent().toast(
                        this,
                        resources.getString(R.string.enter_multiples_of_100)
                    )
                    enterAmount.text?.clear()
                }

            }

        }

        buttonDepositOrWithdraw.setOnClickListener {
            readAmount()


        }
        enterAmount.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    readAmount()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@OnKeyListener true
                }
            }
            false
        })



        buttonBackToMainPage.setOnClickListener {
            SharedPreferenceAccess(this).clearPreference()
            val cancelIntent =
                Intent(this@DepositOrWithdrawActivity, AccountNumberActivity::class.java)
            ToastAndIntent().intent(cancelIntent)
            startActivity(cancelIntent)
            finish()
        }
    }

    override fun onBackPressed() {
    }


}

