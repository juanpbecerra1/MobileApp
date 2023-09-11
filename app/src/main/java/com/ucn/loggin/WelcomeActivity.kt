package com.ucn.loggin

import android.os.Bundle
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity


class WelcomeActivity : AppCompatActivity() {

    private lateinit var lblWelcome: TextView
    companion object{
        const val names = "names"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcom_activity)
        lblWelcome = findViewById(R.id.textWellcome)
         val user = intent.getStringExtra("names")
        lblWelcome.text = "$user!"
    }
}


