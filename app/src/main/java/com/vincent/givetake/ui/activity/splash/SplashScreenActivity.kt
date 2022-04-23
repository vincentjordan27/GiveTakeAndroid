package com.vincent.givetake.ui.activity.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vincent.givetake.R
import com.vincent.givetake.ui.activity.home.MainActivity
import com.vincent.givetake.ui.activity.login.LoginActivity
import com.vincent.givetake.ui.activity.register.RegisterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        scope.launch {
            delay(3000)
            Intent(this@SplashScreenActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
            finish()
        }
    }
}