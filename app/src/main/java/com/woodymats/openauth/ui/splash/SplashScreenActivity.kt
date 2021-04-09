package com.woodymats.openauth.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import com.woodymats.openauth.MainActivity
import com.woodymats.openauth.databinding.ActivitySplashScreenBinding
import com.woodymats.openauth.ui.login.LoginActivity
import com.woodymats.openauth.utils.IS_USER_LOGGED_IN
import com.woodymats.openauth.utils.PREFERENCES

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        binding.motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                val userLoggedIn = preferences.getBoolean(IS_USER_LOGGED_IN, false)
                if (userLoggedIn) {
                    startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    finish()
                } else {
                    startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    finish()
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

        })
    }
}