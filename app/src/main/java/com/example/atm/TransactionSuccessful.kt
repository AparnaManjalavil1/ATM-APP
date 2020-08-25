package com.example.atm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityTransactionSuccessfullBinding
import kotlinx.android.synthetic.main.activity_transaction_successfull.*
import kotlinx.android.synthetic.main.activity_transaction_successfull.buttonBackToMainPage

class TransactionSuccessful : AppCompatActivity() {
    private lateinit var binding:ActivityTransactionSuccessfullBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_transaction_successfull)
        transactionSuccessfulImage.visibility= View.VISIBLE
        transactionSuccessfulImage.setImageResource(R.drawable.transaction_successfull)
        val receivedData=intent.extras
        val showAccountNumber= receivedData?.getLong("transaction_account_number")
        val showAmount=receivedData?.getInt("transaction_amount")
        val showTransactionId=receivedData?.getLong("transaction_id")
        transferAmount.text= getString(R.string.mini_statement_amount,showAmount.toString())
        transferAccountNumber.text=getString(R.string.transfer_account_number,showAccountNumber.toString())
        transferTransactionId.text=showTransactionId.toString()
        buttonShowBalance.setOnClickListener {

            startActivity(Intent(this@TransactionSuccessful,BalanceActivity::class.java))

        }
        buttonBackToMainPage.setOnClickListener {

            val cancelTransactionIntent =
                Intent(this@TransactionSuccessful, AccountNumberActivity::class.java)
            cancelTransactionIntent.addCategory(Intent.CATEGORY_HOME)
            cancelTransactionIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(cancelTransactionIntent)
            finish()
        }
    }
    override fun onBackPressed() {

    }


}