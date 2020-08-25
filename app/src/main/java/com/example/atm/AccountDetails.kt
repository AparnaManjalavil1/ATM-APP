package com.example.atm

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountDetails(
    @PrimaryKey
    var accountNumber: Long,


    @ColumnInfo(name = "ACCOUNT_PASSWORD")
    var accountPassword: Int,

    @ColumnInfo(name = "BALANCE")
    var amount: Int,


    @ColumnInfo(name = "PHONE_NUMBER")
    var phoneNumber: Long


) {
    constructor() : this(0L, 0, 0,0L)
}
