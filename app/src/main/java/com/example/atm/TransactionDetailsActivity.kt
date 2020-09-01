package com.example.atm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityTransactionDetailsBinding
import kotlinx.android.synthetic.main.activity_transaction_details.*

class TransactionDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTransactionDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_details)
        val mAccountNumber = SharedPreferenceAccess(this).getInstanceObject(this).getPreference()
        val mAccountNumberToTransfer = SharedPreferenceAccess(
            "",
            this@TransactionDetailsActivity
        ).getInstanceObject(this@TransactionDetailsActivity).getPreference()
        val credit = ConfigProperties().getConfigValue(this, "depositRemark")
        val debit = ConfigProperties().getConfigValue(this, "withdrawRemark")
        val transfer = ConfigProperties().getConfigValue(this, "transferRemark")
        val receivedData = intent.extras
        val showDate = receivedData?.getString("date")
        val showRemark = receivedData?.getString("remark")
        val showAmount = receivedData?.getInt("amount")
        val showTime = receivedData?.getString("time")
        showTransactionDate.text = resources.getString(R.string.show_date,showDate)
        showTransactionTime.text = resources.getString(R.string.show_time,showTime)
        showTransactionRemark.text = resources.getString(R.string.show_remark,showRemark)
        if (showRemark == debit) {
            showTransactionAccount.text = getString(R.string.show_atm)
            showTransactionAmount.text = showAmount.toString()
        } else if (showRemark == credit) {
            showTransactionAccount.text = resources.getString(R.string.transfer_account_number,mAccountNumber.toString())
            showTransactionAmount.text = resources.getString(R.string.show_amount,showAmount.toString())
        } else if (showRemark == transfer) {

            showTransactionAccount.text = resources.getString(R.string.transfer_account_number,mAccountNumberToTransfer.toString())
            showTransactionAmount.text = resources.getString(R.string.show_amount,showAmount.toString())
        } else
            showTransactionAccount.error =
                resources.getString(R.string.error_invalid_account_number)


        btnCancelTransactionDetails.setOnClickListener {
            val transactionDetailsIntent =
                Intent(this@TransactionDetailsActivity, MiniStatementActivity::class.java)
            ToastAndIntent().intent(transactionDetailsIntent)
            startActivity(transactionDetailsIntent)
            finish()
        }

    }

    override fun onBackPressed() {
    }
}