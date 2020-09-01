package com.example.atm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityDepositBinding
import kotlinx.android.synthetic.main.activity_deposit.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DepositActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var binding: ActivityDepositBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deposit)
        fun readDepositAmount() {
            val stringCreditAmount = enterDepositAmount.text.toString()
            if (stringCreditAmount.trim().isEmpty()) {
                enterDepositAmount.requestFocus()
                enterDepositAmount.error = resources.getString(R.string.enter_amount)
            }
            else {
                val creditAmount = Integer.parseInt(stringCreditAmount)
                if (creditAmount % 100 == 0) {

                    val mAccountNumber =
                        SharedPreferenceAccess(this@DepositActivity).getInstanceObject(this@DepositActivity)
                            .getPreference()
                    val credit =
                        ConfigProperties().getConfigValue(this, "depositRemark")
                    db = DetailsDatabase.getAppDataBase(this)
                    GlobalScope.launch {
                        val balance = db?.details()?.getAmount(mAccountNumber)
                        val creditAfterAmount = creditAmount + balance!!
                        db?.details()?.updateBalance(creditAfterAmount, mAccountNumber)
                        val transactionDate = MiniStatementTable().setTransactionDate()
                        val transactionTime = MiniStatementTable().setTransactionTime()
                        val uniqueId = db?.transactionDetails()?.random()
                        db?.transactionDetails()?.insertTransactionDetails(
                            MiniStatementEntity(
                                uniqueId!!,
                                mAccountNumber,
                                transactionTime,
                                transactionDate,
                                credit,
                                creditAmount
                            )
                        )



                        startActivity(
                            Intent(
                                this@DepositActivity,
                                BalanceActivity::class.java
                            )
                        )
                    }
                } else {
                    ToastAndIntent().toast(
                        this,
                        resources.getString(R.string.enter_multiples_of_100)
                    )
                    enterDepositAmount.text?.clear()
                }

            }

        }

        buttonCredit.setOnClickListener {
            readDepositAmount()


        }
        enterDepositAmount.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
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
            SharedPreferenceAccess(this).clearPreference()
            val depositCancelIntent =
                Intent(this@DepositActivity, AccountNumberActivity::class.java)
            ToastAndIntent().intent(depositCancelIntent)
            startActivity(depositCancelIntent)
            finish()
        }
    }

    override fun onBackPressed() {
    }


}

