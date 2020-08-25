package com.example.atm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_ministatement.*
import kotlinx.android.synthetic.main.activity_transaction_details.*

class TransactionDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)
        val transactionDate=findViewById<TextView>(R.id.showTransactionDate)
       val transactionRemark=findViewById<TextView>(R.id.showTransactionRemark)
        val transactionAccount=findViewById<TextView>(R.id.showTransactionAccount)
        val transactionAmount=findViewById<TextView>(R.id.showTransactionAmount)
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("account_number", Context.MODE_PRIVATE)
        val getAccountNumber = sharedPreferences.getLong("valid accountNumber", 0L)
        val sharedPreferencesTransfer: SharedPreferences =
            this.getSharedPreferences("account_number_transfer", Context.MODE_PRIVATE)
        val getAccountNumberTransfer = sharedPreferencesTransfer.getLong("valid accountNumber transfer", 0L)
        val receivedData=intent.extras
        val showDate= receivedData?.getString("date")
        val showRemark=receivedData?.getString("remark")
        val showAmount=receivedData?.getInt("amount")
        val showTime=receivedData?.getString("time")
        showTransactionDate.text=showDate
        //transactionAmount.text = showAmount.toString()
        showTransactionTime.text=showTime
        showTransactionRemark.text=showRemark
        if(showRemark=="debit") {
            showTransactionAccount.text = getString(R.string.show_atm)
            transactionAmount.text = showAmount.toString()
        }
            else if (showRemark == "credit") {
            showTransactionAccount.text = getAccountNumber.toString()
            transactionAmount.text = showAmount.toString()
        }

            else if (showRemark == "transfer"){

                showTransactionAccount.text = getAccountNumberTransfer.toString()
            transactionAmount.text = showAmount.toString()
        }
            else
                showTransactionAccount.error="INVALID"


        btnCancelTransactionDetails.setOnClickListener {
            val sharedPreferences: SharedPreferences =
                this@TransactionDetailsActivity.getSharedPreferences("account_number",Context.MODE_PRIVATE)

            val editor= sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val pinCancelDetailsIntent = Intent(this@TransactionDetailsActivity, AccountNumberActivity::class.java)
            pinCancelDetailsIntent.addCategory(Intent.CATEGORY_HOME)
            pinCancelDetailsIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(pinCancelDetailsIntent)
            finish()
        }

    }

    override fun onBackPressed() {
    }
}