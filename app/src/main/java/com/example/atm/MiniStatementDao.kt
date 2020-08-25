package com.example.atm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlin.math.abs
import kotlin.random.Random

@Dao
interface MiniStatementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactionDetails(transactionDetails: MiniStatementEntity)

    @Query("SELECT DATE FROM MiniStatementEntity")
    fun getDate(): String

    @Query("SELECT REMARK FROM MiniStatementEntity")
    fun getRemark(): String

    @Query("SELECT AMOUNT FROM MiniStatementEntity")
    fun getTransactionAmount(): Int

    @Query("SELECT COUNT(*)FROM MiniStatementEntity where ACCOUNT_NUMBER =:number")
    fun getRowCount(number: Long): Int

    @Query("SELECT * FROM MiniStatementEntity where ACCOUNT_NUMBER=:number ORDER BY TIME DESC")
    fun getDetails(number: Long): List<MiniStatementEntity>

    fun random(): Long {

        return abs(Random.nextLong())

    }


}