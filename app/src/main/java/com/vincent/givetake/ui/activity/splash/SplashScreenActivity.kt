package com.vincent.givetake.ui.activity.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.R
import com.vincent.givetake.factory.PrefViewModelFactory
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.home.MainActivity
import com.vincent.givetake.ui.activity.login.LoginActivity
import com.vincent.givetake.ui.activity.register.RegisterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val pref = UserPreferences.getInstance(dataStore)
        val factory = PrefViewModelFactory.getInstance(pref)
        val viewModel = ViewModelProvider(this, factory)[SplashViewModel::class.java]


        viewModel.getAccessKey().observe(this@SplashScreenActivity) {
            scope.launch {
                delay(3000)
                if (it == "") {
                    Intent(this@SplashScreenActivity, LoginActivity::class.java).also {
                        startActivity(it)
                    }
                } else {
                    Intent(this@SplashScreenActivity, MainActivity::class.java).also {
                        startActivity(it)
                    }
                }
                finish()
            }
        }
    }
}