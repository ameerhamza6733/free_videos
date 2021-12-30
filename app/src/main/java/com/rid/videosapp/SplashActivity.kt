package com.rid.videosapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Splash Activity that inflates splash activity xml. */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
  private val COUNTER_TIME = 3L
  private val LOG_TAG = "SplashActivity"
  private var secondsRemaining: Long = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    createTimer(COUNTER_TIME)
  }
  private fun createTimer(seconds: Long) {
    val counterTextView: TextView = findViewById(R.id.timer)
    val countDownTimer: CountDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
      @SuppressLint("SetTextI18n")
      override fun onTick(millisUntilFinished: Long) {
        secondsRemaining = millisUntilFinished / 1000 + 1
       // counterTextView.text = "App is done loading in: $secondsRemaining"
      }

      @SuppressLint("SetTextI18n")
      override fun onFinish() {
        secondsRemaining = 0
       // counterTextView.text = "Done."

        val application = application as? MyApplication

        if (application == null) {
          Log.e(LOG_TAG, "Failed to cast application to MyApplication.")
          startMainActivity()
          return
        }

        // Show the app open ad.
        application.showAdIfAvailable(
          this@SplashActivity,
          object : MyApplication.OnShowAdCompleteListener {
            override fun onShowAdComplete() {
              startMainActivity()
            }
          })
      }
    }
    countDownTimer.start()
  }
  fun startMainActivity() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
  }
}
