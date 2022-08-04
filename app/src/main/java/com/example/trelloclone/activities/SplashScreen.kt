package com.example.trelloclone.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.trelloclone.databinding.ActivitySplashScreenBinding
import com.example.trelloclone.firebase.FirestoreClass


class SplashScreen : AppCompatActivity() {

    private var binding : ActivitySplashScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //fullScreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )

        val typeface : Typeface = Typeface.createFromAsset(assets,"BattleBotsItalic.ttf")

        binding?.tvAppName?.typeface = typeface

        Handler().postDelayed({

            if(FirestoreClass().getCurrentUserId().isBlank())
                startActivity(Intent(this, IntroActivity::class.java))
            else
                startActivity(Intent(this, MainActivity::class.java))
            finish()
        },2000)
    }
}