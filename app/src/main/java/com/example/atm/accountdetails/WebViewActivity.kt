package com.example.atm.accountdetails

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.example.atm.R
import com.example.atm.util.ConfigUtil
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {
    //private lateinit var binding:ActivityWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val toolBar=findViewById<Toolbar>(R.id.toolbar)
        val webView=findViewById<WebView>(R.id.webView)
        val btnCancel=findViewById<ImageView>(R.id.cancel)
        val webUrl=intent.getStringExtra(ConfigUtil().websiteUrl)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?, url: String?
            ): Boolean {
                view?.loadUrl(url!!)
                return true
            }

        }
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled=true
        webSettings.setSupportMultipleWindows(true)
        webView.loadUrl(webUrl!!)
        btnCancel.setOnClickListener {
            val backToAccountNumberPage=Intent(this,AccountNumberActivity::class.java)
            ConfigUtil().intent(backToAccountNumberPage)
            startActivity(backToAccountNumberPage)
            finish()

        }

    }

    override fun onBackPressed() {
    }
}