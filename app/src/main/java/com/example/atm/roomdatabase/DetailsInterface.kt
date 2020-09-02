package com.example.atm.roomdatabase

import androidx.room.*
import com.example.atm.roomdatabase.AccountDetails

@Dao
interface DetailsInterface {
    @Insert(onConflict = OnConflictStrategy.REPLACE)


    suspend fun insertDetails(details: AccountDetails)

    @Query("SELECT * FROM AccountDetails LIMIT 1")
    suspend fun getAllDetails(): List<AccountDetails>

    @Query("SELECT * FROM AccountDetails WHERE accountNumber = :accountNumber")
    suspend fun getDetails(accountNumber: Long): List<AccountDetails>

    @Query("SELECT EXISTS(SELECT * FROM AccountDetails WHERE accountNumber =:accountNumber AND ACCOUNT_PASSWORD = :accountPassword)")
    suspend fun isPasswordExist(accountNumber: Long, accountPassword: Int): Boolean

    @Query("SELECT BALANCE FROM AccountDetails WHERE accountNumber =:accountNumber ")
    suspend fun getAmount(accountNumber: Long): Int

    @Query("SELECT ACCOUNT_PASSWORD FROM AccountDetails WHERE accountNumber =:accountNumber")
    suspend fun getPassword(accountNumber: Long): Int

    @Query("UPDATE AccountDetails SET ACCOUNT_PASSWORD =:accountPassword WHERE accountNumber =:accountNumber")
    suspend fun changePassword(accountPassword: Int, accountNumber: Long)

    @Query("UPDATE AccountDetails SET BALANCE =:amount WHERE accountNumber =:accountNumber")
    suspend fun updateBalance(amount: Int, accountNumber: Long)

    @Query("UPDATE AccountDetails SET `PHONE_NUMBER` =:phoneNumber WHERE accountNumber =:accountNumber")
    suspend fun updatePhoneNumber(phoneNumber: Long, accountNumber: Long)

    @Query("SELECT EXISTS(SELECT * FROM AccountDetails WHERE PHONE_NUMBER =:phoneNumber)")
    suspend fun isPhoneNumberExist(phoneNumber: Long): Boolean


}
