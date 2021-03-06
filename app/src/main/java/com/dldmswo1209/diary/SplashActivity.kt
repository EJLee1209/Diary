package com.dldmswo1209.diary

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val uid = sharedPreferences.getString("user_uid", "")

        when(uid){
            "" ->{
                startActivity(Intent(this, LoginActivity::class.java))
            }
            else ->{
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}