package com.example.atm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        btnDeposit.setOnClickListener(this)
        btnWithdraw.setOnClickListener(this)
        btnTransfer.setOnClickListener(this)
        btnBalanceEnquirey.setOnClickListener(this)
        btnPinChange.setOnClickListener(this)
        btnMini.setOnClickListener(this)
        btnOthers.setOnClickListener(this)
        btnCancel_main.setOnClickListener {
            val cancelIntent = Intent(this@MainActivity, AccountNumberActivity::class.java)
            ToastAndIntent().intent(cancelIntent)
            startActivity(cancelIntent)
        }

    }


    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.btnDeposit, R.id.btnWithdraw, R.id.btnTransfer, R.id.btnBalanceEnquirey, R.id.btnPinChange, R.id.btnMini, R.id.btnOthers -> {

                val buttonClick =
                    ConfigProperties().getConfigValue(this, "pinNumberNavigation")
                val intent = Intent(this, PinNumberActivity::class.java)
                intent.putExtra(buttonClick, view.id)
                startActivity(intent)

            }
            else -> Toast.makeText(this@MainActivity, "INVALID BUTTON", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onBackPressed() {


    }

}
