package com.example.atm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityWithdrawBinding
import kotlinx.android.synthetic.main.activity_withdraw.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class WithdrawActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var binding: ActivityWithdrawBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_withdraw)
        fun readDebitAmount() {
            val stringDebitAmount = enterDebitAmount.text.toString()
            if (stringDebitAmount.trim().isEmpty())
                enterDebitAmount.error = resources.getString(R.string.enter_amount)
            else {
                val debitAmount = Integer.parseInt(stringDebitAmount)
                if (debitAmount % 100 == 0) {
                    val debit = ConfigProperties().getConfigValue(this, "withdrawRemark")
                    val mAccountNumber =
                        SharedPreferenceAccess(this@WithdrawActivity).getInstanceObject(this@WithdrawActivity)
                            .getPreference()
                    db = DetailsDatabase.getAppDataBase(this)
                    GlobalScope.launch {
                        val balance = db?.details()?.getAmount(mAccountNumber)
                        val debitBalance: Int = balance!!
                        if (debitAmount <= debitBalance) {
                            val debitAfterAmount = debitBalance - debitAmount
                            db?.details()?.updateBalance(debitAfterAmount, mAccountNumber)
                            val transactionDate = MiniStatementTable().setTransactionDate()
                            val transactionTime = MiniStatementTable().setTransactionTime()
                            val uniqueId = db?.transactionDetails()?.random()
                            db?.transactionDetails()?.insertTransactionDetails(
                                MiniStatementEntity(
                                    uniqueId!!,
                                    mAccountNumber,
                                    transactionTime,
                                    transactionDate,
                                    debit,
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
                                ToastAndIntent().toast(
                                    this@WithdrawActivity,
                                    resources.getString(R.string.insufficient_balance)
                                )
                                enterDebitAmount.text?.clear()
                            }

                        }

                    }

                } else {
                    ToastAndIntent().toast(
                        this@WithdrawActivity,
                        resources.getString(R.string.enter_multiples_of_100)
                    )

                }
                enterDebitAmount.text?.clear()
            }
        }
        binding.buttonDebit.setOnClickListener {
            readDebitAmount()


        }
        binding.enterDebitAmount.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
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
            SharedPreferenceAccess(this).clearPreference()
            val withdrawCancelIntent =
                Intent(this@WithdrawActivity, AccountNumberActivity::class.java)
            ToastAndIntent().intent(withdrawCancelIntent)
            startActivity(withdrawCancelIntent)
            finish()

        }
    }

    override fun onBackPressed() {
    }

}