package com.example.atm.accountdetails


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.atm.CreateNewAccountActivity
import com.example.atm.R
import com.example.atm.dashboard.MainActivity
import com.example.atm.databinding.ActivityAccountNumberBinding
import com.example.atm.roomdatabase.AccountDetails
import com.example.atm.roomdatabase.DetailsDatabase
import com.example.atm.util.ConfigUtil
import com.example.atm.util.SharedPreferenceAccess
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_account_number.*
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AccountNumberActivity : AppCompatActivity(), CoroutineScope, PermissionListener {
    private var db: DetailsDatabase? = null
    private var listAccountNumber: List<AccountDetails>? = null


    /**
     *note:Back CoroutineScope for implementing background thread
     * Job is used to start the coroutine
     */
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private lateinit var binding: ActivityAccountNumberBinding

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_account_number
        )

        fun readAccountNumber() {
            val stringAccountNumber = enterAccountNumber.text.toString()
            try {
                val accountNumber = stringAccountNumber.toLong()
                db =
                    DetailsDatabase.getAppDataBase(this)
                GlobalScope.launch {
                    listAccountNumber = db?.details()?.getDetails(accountNumber)
                    if (listAccountNumber?.size!! > 0) {
                        SharedPreferenceAccess(this@AccountNumberActivity)
                            .getInstanceObject(this@AccountNumberActivity)
                            .setPreference(accountNumber)
                        this@AccountNumberActivity.runOnUiThread {
                            val alertView = LayoutInflater
                                .from(this@AccountNumberActivity)
                                .inflate(R.layout.alert_dialog, null)
                            val alertBoxBuilder =
                                AlertDialog.Builder(this@AccountNumberActivity)
                                    .setView(alertView)
                            val alertBox = alertBoxBuilder.create()
                            val imm = getSystemService(
                                Context.INPUT_METHOD_SERVICE
                            )
                                    as InputMethodManager
                            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                            alertBox.show()
                            alertBox.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_alert_box)
                            alertBox.window?.setLayout(950, 575)
                            alertView.savings.setOnClickListener {
                                alertBox.dismiss()
                                val accountNumberIntent =
                                    Intent(
                                        this@AccountNumberActivity,
                                        MainActivity::class.java
                                    )
                                startActivity(accountNumberIntent)
                            }
                            alertView.current.setOnClickListener {
                                alertBox.dismiss()
                                val accountNumberIntent =
                                    Intent(
                                        this@AccountNumberActivity,
                                        MainActivity::class.java
                                    )
                                startActivity(accountNumberIntent)
                            }
                        }

                    } else {
                        this@AccountNumberActivity.runOnUiThread {
                            enterAccountNumber.requestFocus()
                            enterAccountNumber.error =
                                resources.getString(R.string.error_invalid_account_number)
                        }
                    }

                }
            } catch (ex: NumberFormatException) {
                if (stringAccountNumber.trim()
                        .isEmpty()
                ) {
                    enterAccountNumber.requestFocus()
                    enterAccountNumber.error =
                        resources.getString(R.string.error_empty_account_number)
                } else if (stringAccountNumber.trim().length < 10) {
                    enterAccountNumber.error =
                        resources.getString(R.string.error_invalid_account_number)
                }

            }
        }

        val spannableString = SpannableString(createNewAccount.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                val createAccountIntent =
                    Intent(
                        this@AccountNumberActivity,
                        CreateNewAccountActivity::class.java
                    )
                ConfigUtil().intent(createAccountIntent)
                startActivity(createAccountIntent)
                finish()

            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, 1, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        createNewAccount.setText(spannableString, TextView.BufferType.SPANNABLE)
        createNewAccount.highlightColor = getColor(R.color.colorWhite)
        createNewAccount.movementMethod = LinkMovementMethod.getInstance()
        btnSubmit.setOnClickListener {
            readAccountNumber()
        }

        website.setOnClickListener {
            val webUrl = getString(R.string.website_url)
            val webIntent = Intent(this, WebViewActivity::class.java)
            webIntent.putExtra(ConfigUtil().websiteUrl, webUrl)
            startActivity(webIntent)
        }

        facebook.setOnClickListener {
            val facebookIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_url)))
            startActivity(facebookIntent)
        }

        mail.setOnClickListener {
            val mailIntent = Intent(Intent.ACTION_SENDTO)
            mailIntent.data = Uri.parse("mailto:")
            val recipients = arrayOf(getString(R.string.mail_id))
            mailIntent.putExtra(Intent.EXTRA_EMAIL, recipients)
            try {
                startActivity(Intent.createChooser(mailIntent, ConfigUtil().sendingText))
            } catch (ex: ActivityNotFoundException) {
            }
        }

        binding.call.setOnClickListener {
            Dexter.withActivity(this)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(this)
                .check()
        }

        map.setOnClickListener {
            val uri =
                Uri.parse(
                    getString(R.string.location_coordinates) + Uri.encode(
                        getString(
                            R.string.location_name
                        )
                    )
                )
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage(getString(R.string.map_package_name))
            mapIntent.resolveActivity(packageManager)?.let {
                startActivity(mapIntent)
            }

        }

        enterAccountNumber.setOnKeyListener(View.OnKeyListener { view, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                readAccountNumber()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                return@OnKeyListener true

            }
            false
        })

    }

    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
        }
        this.doubleBackToExitPressedOnce = true
        ConfigUtil()
            .toast(this, resources.getString(R.string.text_double_exit))
        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)

    }

    fun callPhone() {
        try {
            val callIntent = Intent(
                Intent.ACTION_CALL,
                Uri.parse(
                    ConfigUtil().telephoneNumber +
                            getString(R.string.telephone_number)
                )
            )
            startActivity(callIntent)
        } catch (ex: ActivityNotFoundException) {
            Log.e(resources.getString(R.string.show_atm), "Exception : ${ex.message}")
        }
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        callPhone()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {
        token?.continuePermissionRequest()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        if (response!!.isPermanentlyDenied) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.dialog_enable_settings)
            builder.setPositiveButton(resources.getString(R.string.dialog_go_to_settings)) { dialogInterface, which ->
                val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                settingsIntent.data = Uri.parse("package:$packageName")
                startActivity(settingsIntent)
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()

        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.dialog_permission_denied)
            builder.setMessage(R.string.dialog_permission_denied_message)
            builder.setPositiveButton(resources.getString(R.string.dialog_ok)) { dialogInterface, which ->
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

}








