package com.bolunevdev.kinon.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock.sleep
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.bolunevdev.kinon.R
import java.util.concurrent.Executors

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        findViewById<LottieAnimationView>(R.id.start_animation)
        val lottieAnimationView: LottieAnimationView = findViewById(R.id.start_animation)
        lottieAnimationView.playAnimation()

        Executors.newSingleThreadExecutor().execute {
            val startDelay = 1500L
            sleep(startDelay)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}