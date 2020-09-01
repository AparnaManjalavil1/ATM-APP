package com.example.atm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityTransferBinding
import kotlinx.android.synthetic.main.activity_transfer.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class TransferActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var binding: ActivityTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer)

        db = DetailsDatabase.getAppDataBase(this)
        val mAccountNumberFromTransfer =
            SharedPreferenceAccess(this@TransferActivity).getInstanceObject(this@TransferActivity)
                .getPreference()
        GlobalScope.launch {
            val debitedBalanceInt = db?.details()?.getAmount(mAccountNumberFromTransfer)
            val debitBalance = debitedBalanceInt.toString()
            currentBalance.text = getString(R.string.current_balance, debitBalance)

        }



        fun readTransferDetails() {
            val stringAccountNumberToTransfer = enterAccountNumberToTransfer.text.toString()
            val stringAmountToTransfer = enterAmountToTransfer.text.toString()
            try {
                val accountNumberToTransfer = stringAccountNumberToTransfer.toLong()
                SharedPreferenceAccess("accountNumberToTransfer", this).setPreference(
                    accountNumberToTransfer
                )


                val amountToTransfer = Integer.parseInt(stringAmountToTransfer)
                val transfer = ConfigProperties().getConfigValue(this, "transferRemark")

                GlobalScope.launch {
                    val debitedBalance = db?.details()?.getAmount(mAccountNumberFromTransfer)

                    if (accountNumberToTransfer != mAccountNumberFromTransfer) {

                        val amountAfterDebitTransfer = debitedBalance?.minus(amountToTransfer)
                        if (debitedBalance!! >= amountToTransfer) {
                            db?.details()?.updateBalance(
                                amountAfterDebitTransfer!!,
                                mAccountNumberFromTransfer
                            )
                            val transactionDate = MiniStatementTable().setTransactionDate()
                            val transactionTime = MiniStatementTable().setTransactionTime()
                            val uniqueId = db?.transactionDetails()?.random()
                            db?.transactionDetails()?.insertTransactionDetails(
                                MiniStatementEntity(
                                    uniqueId!!,
                                    mAccountNumberFromTransfer,
                                    transactionTime,
                                    transactionDate,
                                    transfer,
                                    amountToTransfer
                                )
                            )

                            val transferIntent = Intent(
                                this@TransferActivity,
                                TransactionSuccessful::class.java
                            )
                            transferIntent.putExtra("transaction_id", uniqueId)
                            transferIntent.putExtra("transaction_amount", amountToTransfer)
                            transferIntent.putExtra(
                                "transaction_account_number",
                                accountNumberToTransfer
                            )
                            startActivity(transferIntent)
                        } else {
                            this@TransferActivity.runOnUiThread {
                                Toast.makeText(
                                    this@TransferActivity,
                                    resources.getString(R.string.error_insufficient_balance_to_transfer),
                                    Toast.LENGTH_LONG
                                ).show()
                                enterAmountToTransfer.text?.clear()
                            }

                        }
                    } else {
                        this@TransferActivity.runOnUiThread {
                            Toast.makeText(
                                this@TransferActivity,
                                resources.getString(R.string.error_transfer_denied),
                                Toast.LENGTH_LONG
                            ).show()
                            enterAccountNumberToTransfer.requestFocus()
                            enterAccountNumberToTransfer.text?.clear()

                        }

                    }

                }


            } catch (ex: Exception) {
                if (stringAccountNumberToTransfer.trim()
                        .isEmpty() && stringAmountToTransfer.trim().isEmpty()
                )
                {

                    enterAccountNumberToTransfer.requestFocus()
                    enterAccountNumberToTransfer.error =
                        resources.getString(R.string.error_empty_account_number)
                    enterAmountToTransfer.error =
                        resources.getString(R.string.error_amount_to_transfer)


                }
                else if(stringAccountNumberToTransfer.trim()
                        .isEmpty()
                ){
                    enterAccountNumberToTransfer.requestFocus()
                    enterAccountNumberToTransfer.error =
                        resources.getString(R.string.error_invalid_account_number)
                }
                else if((stringAmountToTransfer.trim().isEmpty())){
                    enterAmountToTransfer.requestFocus()
                    enterAmountToTransfer.error =
                        resources.getString(R.string.error_amount_to_transfer)

                }


            }
        }
        buttonTransfer.setOnClickListener {
            readTransferDetails()


        }
        enterAmountToTransfer.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if(enterAccountNumberToTransfer.length()==0&&enterAmountToTransfer.length()!=0){
                enterAccountNumberToTransfer.requestFocus()
                enterAccountNumberToTransfer.error=resources.getString(R.string.error_invalid_account_number)
                enterAmountToTransfer.text?.clear()
            }
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    readTransferDetails()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@OnKeyListener true
                }
            }
            false
        })
        buttonBackToMainPage.setOnClickListener {

            val cancelTransferIntent =
                Intent(this@TransferActivity, AccountNumberActivity::class.java)
            ToastAndIntent().intent(cancelTransferIntent)
            startActivity(cancelTransferIntent)
            finish()
        }
    }

    override fun onBackPressed() {

    }


}