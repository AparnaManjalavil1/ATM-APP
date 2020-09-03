package com.example.atm.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class ConfigUtil {
    val depositOrWithdrawIntentName = "DepositOrWithdraw"
    val depositRemark = "credit"
    val withdrawRemark = "debit"
    val transferRemark = "transfer"
    val date = "date"
    val time = "time"
    val remark = "remark"
    val amount = "amount"
    val buttonClick = "button_clicked"
    val sharedPreferenceName = "accountNumberToTransfer"
    val transactionId = "transaction_id"
    val transactionAccountNumber = "transaction_account_number"
    private val transactionDate = Date()

    fun intent(intent: Intent) {
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    fun toast(context: Context, text: String) {
        Toast.makeText(context.applicationContext, text, Toast.LENGTH_LONG).show()
    }

    fun getTransactionDate(): String {
        val transactionDateFormat =
            SimpleDateFormat("MMM dd yyy ", Locale.getDefault())
        return transactionDateFormat.format(transactionDate)
    }

    fun getTransactionTime(): String {
        val transactionTimeFormat =
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        return transactionTimeFormat.format(transactionDate)
    }
}

