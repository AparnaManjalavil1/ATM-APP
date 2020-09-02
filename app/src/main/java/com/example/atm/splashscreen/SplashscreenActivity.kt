package com.example.atm.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.atm.roomdatabase.AccountDetails
import com.example.atm.accountdetails.AccountNumberActivity
import com.example.atm.roomdatabase.DetailsDatabase
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SplashscreenActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DetailsDatabase.getAppDataBase(this)
        GlobalScope.launch {
            val details = db?.details()?.getAllDetails()?.size
            if (details == 0) {
                db?.details()?.insertDetails(
                    AccountDetails(
                        accountNumber = 1234567000,
                        accountPassword = 1234,
                        amount = 5000, phoneNumber = 9745540753
                    )
                )
                db?.details()?.insertDetails(
                    AccountDetails(
                        accountNumber = 8910112000,
                        accountPassword = 5678,
                        amount = 25000,
                        phoneNumber = 9539324916

                    )
                )
                db?.details()?.insertDetails(
                    AccountDetails(
                        accountNumber = 2468101000,
                        accountPassword = 2468,
                        amount = 1000,
                        phoneNumber = 9846454176
                    )
                )
                db?.details()?.insertDetails(
                    AccountDetails(
                        accountNumber = 5101520000,
                        accountPassword = 1357,
                        amount = 10000,
                        phoneNumber = 8086853340
                    )
                )
            }
        }

        startActivity(Intent(this@SplashscreenActivity, AccountNumberActivity::class.java))
        finish()
    }
}