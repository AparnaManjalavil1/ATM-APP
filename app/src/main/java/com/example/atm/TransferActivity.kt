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
import com.example.atm.databinding.ActivityTransferBinding
import kotlinx.android.synthetic.main.activity_transfer.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class TransferActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var binding: ActivityTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_transfer)
       // val editAccountNumberToTransfer = findViewById<EditText>(R.id.editTransferAccountNumber)
        //val editAmountToTransfer = findViewById<EditText>(R.id.amountToTransfer)
       // val currentBalance=findViewById<TextView>(R.id.current_balance_view)


        db = DetailsDatabase.getAppDataBase(this)
                val sharedPreferences: SharedPreferences =
                    this.getSharedPreferences("account_number", Context.MODE_PRIVATE)
                val getAccountNumberFromTransfer =
                    sharedPreferences.getLong("valid accountNumber", 0L)
                GlobalScope.launch {
                    val debitedBalanceInt = db?.details()?.getAmount(getAccountNumberFromTransfer)
                    val debitBalance=debitedBalanceInt.toString()
                    currentBalance.text =getString(R.string.current_balance,debitBalance)

                }



        fun readTransferDetails() {
            val stringAccountNumberToTransfer = enterAccountNumberToTransfer.text.toString()
            val stringAmountToTransfer = enterAmountToTransfer.text.toString()
            try {
                val accountNumberToTransfer = stringAccountNumberToTransfer.toLong()
                val sharedPreferencesTransfer: SharedPreferences =
                    this@TransferActivity.getSharedPreferences(
                        "account_number_transfer",
                        Context.MODE_PRIVATE
                    )
                val editor: SharedPreferences.Editor = sharedPreferencesTransfer.edit()
                editor.putLong("valid accountNumber transfer", accountNumberToTransfer)
                editor.apply()


                    val amountToTransfer = Integer.parseInt(stringAmountToTransfer)
                    //  db = DetailsDatabase.getAppDataBase(this
                    //  val sharedPreferences: SharedPreferences =
                    //     this.getSharedPreferences("account_number", Context.MODE_PRIVATE)
                    //val getAccountNumberFromTransfer =
                    //    sharedPreferences.getLong("valid accountNumber", 0L)
                    GlobalScope.launch {
                        val debitedBalance = db?.details()?.getAmount(getAccountNumberFromTransfer)
                        // current_balance.text= debitedBalance.toString()
                        if (accountNumberToTransfer != getAccountNumberFromTransfer) {

                            val amountAfterDebitTransfer = debitedBalance?.minus(amountToTransfer)
                            if (amountAfterDebitTransfer!! >= 1000 && debitedBalance > amountToTransfer) {
                                db?.details()?.updateBalance(
                                    amountAfterDebitTransfer!!,
                                    getAccountNumberFromTransfer
                                )
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
                                        getAccountNumberFromTransfer,
                                        transactionTime,
                                        transactionDate,
                                        "transfer",
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
                                enterAccountNumberToTransfer.text?.clear()

                            }

                        }

                    }


            }
            catch (ex:Exception){
                if (stringAccountNumberToTransfer.trim()
                        .isEmpty() || stringAccountNumberToTransfer.trim().length < 10
                )
                    enterAccountNumberToTransfer.error =
                        resources.getString(R.string.error_invalid_account_number)
                else if (stringAmountToTransfer.trim().isEmpty())
                    enterAmountToTransfer.error =
                        resources.getString(R.string.error_amount_to_transfer)

            }
        }
        buttonTransfer.setOnClickListener {
            readTransferDetails()


        }
        enterAmountToTransfer.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if(keyEvent.action==KeyEvent.ACTION_DOWN) {
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
           // val sharedPreferences: SharedPreferences =
            //    this@TransferActivity.getSharedPreferences("account_number", Context.MODE_PRIVATE)

            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val cancelTransferIntent =
                Intent(this@TransferActivity, AccountNumberActivity::class.java)
            cancelTransferIntent.addCategory(Intent.CATEGORY_HOME)
            cancelTransferIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(cancelTransferIntent)
            finish()
        }
    }

    override fun onBackPressed() {

    }


}