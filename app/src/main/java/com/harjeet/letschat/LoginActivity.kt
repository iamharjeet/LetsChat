package com.harjeet.letschat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import harjeet.chitForChat.databinding.ActivityLoginBinding


/* Entering mobile number from user and go for verification */
class LoginActivity : AppCompatActivity() {
     lateinit var binding: ActivityLoginBinding
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {

            if(!binding.edtPhoneNumber.text.equals("")){
            startActivity(Intent(this@LoginActivity,CodeVerificationActivity::class.java)
                .putExtra(MyConstants.PHONE_NUMBER,binding.edtPhoneNumber.text.toString())
                .putExtra(MyConstants.COUNTRY_CODE,"+${binding.ccp.selectedCountryCode.toString()}")
            )
        }else{
            MyUtils.showToast(this,"Please enter your mobile number")
        }
        }
    }


}