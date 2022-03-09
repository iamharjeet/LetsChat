package com.harjeet.letschat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import harjeet.chitForChat.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            if (MyUtils.getBooleanValue(this@SplashActivity, MyConstants.IS_LOGIN)) {
                finish()
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            } else {
                finish()
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
        }, 3000)
    }
}