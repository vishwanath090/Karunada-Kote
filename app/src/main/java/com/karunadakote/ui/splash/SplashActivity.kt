package com.karunadakote.ui.splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.karunadakote.databinding.ActivitySplashBinding
import com.karunadakote.ui.auth.LoginActivity
import com.karunadakote.ui.fortlist.FortListActivity
import com.karunadakote.data.local.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        startAnimations()

        binding.root.postDelayed({
            val session = SessionManager(this)
            val dest = if (session.isLoggedIn()) FortListActivity::class.java
            else LoginActivity::class.java
            startActivity(Intent(this, dest))

            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )

            finish()

        }, 4000)
    }

    private fun startAnimations() {

        binding.contentContainer.alpha = 0f

        binding.contentContainer.animate()
            .alpha(1f)
            .setDuration(1400)
            .start()

        ObjectAnimator.ofFloat(
            binding.ivFort,
            "scaleX",
            1f,
            1.08f
        ).apply {

            duration = 4200

            repeatMode = ObjectAnimator.REVERSE

            repeatCount = ObjectAnimator.INFINITE

            interpolator =
                AccelerateDecelerateInterpolator()

            start()
        }

        ObjectAnimator.ofFloat(
            binding.ivFort,
            "scaleY",
            1f,
            1.08f
        ).apply {

            duration = 2600

            repeatMode = ObjectAnimator.REVERSE

            repeatCount = ObjectAnimator.INFINITE

            interpolator =
                AccelerateDecelerateInterpolator()

            start()
        }
    }
}