package com.example.atm.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.accountdetails.AccountNumberActivity
import com.example.atm.util.ConfigUtil
import com.example.atm.readpinnumber.PinNumberActivity
import com.example.atm.R
import com.example.atm.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        btnDeposit.setOnClickListener(this)
        btnWithdraw.setOnClickListener(this)
        btnTransfer.setOnClickListener(this)
        btnBalanceEnquirey.setOnClickListener(this)
        btnPinChange.setOnClickListener(this)
        btnMini.setOnClickListener(this)
        btnOthers.setOnClickListener(this)
        btnCancel_main.setOnClickListener {
            val cancelIntent = Intent(
                this@MainActivity,
                AccountNumberActivity::class.java
            )
            ConfigUtil().intent(cancelIntent)
            startActivity(cancelIntent)
        }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnDeposit, R.id.btnWithdraw, R.id.btnTransfer, R.id.btnBalanceEnquirey,
            R.id.btnPinChange, R.id.btnMini, R.id.btnOthers -> {
                val intent = Intent(this, PinNumberActivity::class.java)
                intent.putExtra(ConfigUtil().buttonClick, view.id)
                startActivity(intent)
            }
            else -> ConfigUtil().toast(this@MainActivity, "INVALID BUTTON")
        }

    }

    override fun onBackPressed() {

    }
}
