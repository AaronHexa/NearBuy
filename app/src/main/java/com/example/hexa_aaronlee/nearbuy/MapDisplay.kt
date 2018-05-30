package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MapDisplay : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, MainPage::class.java))
        finish()
    }
}
