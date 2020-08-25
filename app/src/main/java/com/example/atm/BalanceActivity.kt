package com.example.atm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityBalanceBinding
import kotlinx.android.synthetic.main.activity_balance.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BalanceActivity : AppCompatActivity(), CoroutineScope {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var binding:ActivityBalanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_balance)
       // val balanceDisplay = findViewById<TextView>(R.id.textViewBalanceDisplay)
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("account_number", Context.MODE_PRIVATE)
        val getAccountNumber = sharedPreferences.getLong("valid accountNumber", 0L)
        db = DetailsDatabase.getAppDataBase(this)
        GlobalScope.launch {
            val balance = db?.details()?.getAmount(getAccountNumber)


            /*
        spannableString : markup the text
         */
            val spannableString = SpannableString(balanceDisplay.text)
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    balanceDisplay.text = balance.toString()


                }
            }



            spannableString.setSpan(clickableSpan, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            balanceDisplay.setText(spannableString, TextView.BufferType.SPANNABLE)
            balanceDisplay.movementMethod = LinkMovementMethod.getInstance()
            buttonBackToMainPage.setOnClickListener {
                val cancelBalanceIntent =
                    Intent(this@BalanceActivity, AccountNumberActivity::class.java)
                cancelBalanceIntent.addCategory(Intent.CATEGORY_HOME)
                cancelBalanceIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(cancelBalanceIntent)
            }

        }
    }

    override fun onBackPressed() {
    }

}