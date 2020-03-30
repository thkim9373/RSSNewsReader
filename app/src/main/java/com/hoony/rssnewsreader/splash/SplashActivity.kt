package com.hoony.rssnewsreader.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hoony.rssnewsreader.list.ListActivity
import com.hoony.rssnewsreader.R

class SplashActivity : AppCompatActivity() {

    private var handler: Handler? = null
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    override fun onResume() {
        super.onResume()

        runnable = Runnable {
            val intent = Intent(applicationContext, ListActivity::class.java)
            startActivity(intent)
            finish()
        }
        handler = Handler()
        handler?.run {
            postDelayed(runnable, 1300)
        }
    }

    override fun onPause() {
        super.onPause()

        handler?.removeCallbacks(runnable)
    }
}
