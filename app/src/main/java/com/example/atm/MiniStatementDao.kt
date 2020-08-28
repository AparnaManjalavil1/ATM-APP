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

    @Query("SELECT * FROM MiniStatementEntity where ACCOUNT_NUMBER=:number ORDER BY TIME DESC")
    fun getDetails(number: Long): List<MiniStatementEntity>

    fun random(): Long {

        return abs(Random.nextLong())

    }


}