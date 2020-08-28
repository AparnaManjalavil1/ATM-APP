package com.example.atm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        miniStatementRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        miniStatementRecyclerView.setHasFixedSize(false)
        val statementList = ArrayList<MiniStatement>()

        db = DetailsDatabase.getAppDataBase(this)
        val mAccountNumber =
            SharedPreferenceAccess(this@MiniStatementActivity).getInstanceObject(this@MiniStatementActivity)
                .getPreference()
        db = DetailsDatabase.getAppDataBase(this)

        GlobalScope.launch {
            val currentBalanceInt = db?.details()?.getAmount(mAccountNumber)
            val currentBalance = currentBalanceInt.toString()
            val entity = db?.transactionDetails()?.getDetails(mAccountNumber)
            val entitySiz = entity?.size
            val entitySize = entitySiz?.minus(1)
            val adapter = MiniStatementAdapter(statementList, this@MiniStatementActivity)
            miniStatementRecyclerView.adapter = adapter
            if (entitySiz == 0) {
                currentBalanceDisplay.text = getString(R.string.current_balance, currentBalance)
                noTransactionDisplay.text = getString(R.string.mini_statement_no_transaction)


            } else if (entitySiz == 1) {
                statementList.add(
                    MiniStatement(
                        entity[0].transactionDate,
                        entity[0].transactionTime,
                        entity[0].remark,
                        entity[0].balance

                    )
                )
                currentBalanceDisplay.text = getString(R.string.current_balance, currentBalance)
            } else {
                currentBalanceDisplay.text = getString(R.string.current_balance, currentBalance)

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
            SharedPreferenceAccess(this).clearPreference()
            val pinCancelDetailsIntent =
                Intent(this@MiniStatementActivity, AccountNumberActivity::class.java)
            ToastAndIntent().intent(pinCancelDetailsIntent)
            startActivity(pinCancelDetailsIntent)
            finish()
        }

    }


    override fun onItemClick(mini: MiniStatement, position: Int) {
        val itemClickIntent = Intent(this, TransactionDetailsActivity::class.java)
        itemClickIntent.putExtra("date", mini.transactionDate)
        itemClickIntent.putExtra("time", mini.transactionTime)
        itemClickIntent.putExtra("remark", mini.remark)
        itemClickIntent.putExtra("amount", mini.amount)

        startActivity(itemClickIntent)
    }

    override fun onBackPressed() {
    }
}
