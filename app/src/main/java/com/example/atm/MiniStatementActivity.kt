package com.example.atm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.ministatementrecyclerview.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MiniStatementActivity : AppCompatActivity(), CoroutineScope,
    MiniStatementAdapter.OnItemClickListener {
    private var db: DetailsDatabase? = null
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ministatementrecyclerview)
        val recyclerView=findViewById<RecyclerView>(R.id.miniStatementRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(false)
        val statementList = ArrayList<MiniStatement>()

        db = DetailsDatabase.getAppDataBase(this)
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("account_number", Context.MODE_PRIVATE)
        val getAccountNumber = sharedPreferences.getLong("valid accountNumber", 0L)
        db = DetailsDatabase.getAppDataBase(this)
        val currentBalanceDisplay=findViewById<TextView>(R.id.text_transaction_display)
        val noTransactionDisplay=findViewById<TextView>(R.id.text_no_transaction_display)
        val changeView=findViewById<ConstraintLayout>(R.id.constraintLayout)

        GlobalScope.launch {
            val currentBalanceInt= db?.details()?.getAmount(getAccountNumber)
            val currentBalance=currentBalanceInt.toString()
            val entity = db?.transactionDetails()?.getDetails(getAccountNumber)
            val entitySiz = entity?.size
            val entitySize = entitySiz?.minus(1)
            val adapter = MiniStatementAdapter(statementList, this@MiniStatementActivity)
            miniStatementRecyclerView.adapter = adapter
            if(entitySiz==0)
            {
                currentBalanceDisplay.text=getString(R.string.current_balance,currentBalance)
                noTransactionDisplay.text=getString(R.string.mini_statement_no_transaction)


            }
            else if(entitySiz==1){
                statementList.add(
                    MiniStatement(
                        entity[0].transactionDate,
                        entity[0].transactionTime,
                        entity[0].remark,
                        entity[0].balance

                    )
                )
                currentBalanceDisplay.text=getString(R.string.current_balance,currentBalance)
            }


            else {
                currentBalanceDisplay.text=getString(R.string.current_balance,currentBalance)

                var count = 0
                for (i in 0..entitySize!!) {
                    statementList.add(
                        MiniStatement(
                            entity[i].transactionDate,
                            entity[i].transactionTime,
                            entity[i].remark,
                            entity[i].balance

                        )
                    )
                    count += 1
                    if (count == 10)
                        break

                }

            }



        }
        btnCancel.setOnClickListener {
            val sharedPreferences: SharedPreferences =
                this@MiniStatementActivity.getSharedPreferences("account_number",Context.MODE_PRIVATE)

            val editor= sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val pinCancelDetailsIntent = Intent(this@MiniStatementActivity, AccountNumberActivity::class.java)
            pinCancelDetailsIntent.addCategory(Intent.CATEGORY_HOME)
            pinCancelDetailsIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(pinCancelDetailsIntent)
            finish()
        }

    }


    override fun onItemClick(mini: MiniStatement, position: Int) {
        val itemClickIntent = Intent(this, TransactionDetailsActivity::class.java)
        itemClickIntent.putExtra("date", mini.transactionDate)
        itemClickIntent.putExtra("time",mini.transactionTime)
        itemClickIntent.putExtra("remark", mini.remark)
        itemClickIntent.putExtra("amount", mini.amount)

        startActivity(itemClickIntent)
    }
    override fun onBackPressed() {
    }
}
