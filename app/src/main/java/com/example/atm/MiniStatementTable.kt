package com.example.atm

import java.text.SimpleDateFormat
import java.util.*

class MiniStatementTable {

    private val date = Date()
    fun setTransactionDate(): String {
        val transactionDateFormat =
            SimpleDateFormat("MMM dd yyy ", Locale.getDefault())

        return transactionDateFormat.format(date)

    }

    fun setTransactionTime(): String {

        val transactionTimeFormat =
            SimpleDateFormat("hh:mm a", Locale.getDefault())

        return transactionTimeFormat.format(date)

    }
}