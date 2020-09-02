package com.example.atm.transfer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.accountdetails.AccountNumberActivity
import com.example.atm.depositorwithdraw.BalanceActivity
import com.example.atm.util.ConfigUtil
import com.example.atm.R
import com.example.atm.databinding.ActivityTransactionSuccessfullBinding
import kotlinx.android.synthetic.main.activity_transaction_successfull.*

class TransactionSuccessful : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionSuccessfullBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_transaction_successfull
        )
        transactionSuccessfulImage.visibility = View.VISIBLE
        transactionSuccessfulImage.setImageResource(R.drawable.transaction_successfull)
        val receivedData = intent.extras
        val showAccountNumber = receivedData?.getLong(ConfigUtil().transactionAccountNumber)
        val showAmount = receivedData?.getInt(ConfigUtil().amount)
        val showTransactionId = receivedData?.getLong(ConfigUtil().transactionId)
        transferAmount.text = getString(R.string.mini_statement_amount, showAmount.toString())
        transferAccountNumber.text =
            getString(R.string.transfer_account_number, showAccountNumber.toString())
        transferTransactionId.text = showTransactionId.toString()
        buttonShowBalance.setOnClickListener {
            startActivity(
                Intent(
                    this@TransactionSuccessful,
                    BalanceActivity::class.java
                )
            )

        }
        buttonBackToMainPage.setOnClickListener {
            val cancelTransactionIntent =
                Intent(this@TransactionSuccessful, AccountNumberActivity::class.java)
            ConfigUtil().intent(cancelTransactionIntent)
            startActivity(cancelTransactionIntent)
            finish()
        }
    }

    override fun onBackPressed() {

    }

}