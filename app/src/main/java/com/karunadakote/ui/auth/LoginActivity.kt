package com.karunadakote.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karunadakote.data.local.SessionManager
import com.karunadakote.databinding.ActivityLoginBinding
import com.karunadakote.ui.fortlist.FortListActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)

        // Skip login if already logged in
        if (session.isLoggedIn()) {
            goToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabs()
        setupButtons()
        animateIn()
    }

    private fun animateIn() {
        binding.cardAuth.alpha = 0f
        binding.cardAuth.translationY = 60f
        binding.cardAuth.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .start()

        binding.ivLogo.alpha = 0f
        binding.ivLogo.animate()
            .alpha(1f)
            .setDuration(700)
            .start()
    }

    private fun setupTabs() {
        binding.btnTabLogin.setOnClickListener { switchToLogin() }
        binding.btnTabSignup.setOnClickListener { switchToSignup() }
    }

    private fun switchToLogin() {
        isLoginMode = true
        binding.btnTabLogin.alpha = 1f
        binding.btnTabSignup.alpha = 0.45f
        binding.tilName.visibility = View.GONE
        binding.tilPhone.visibility = View.GONE
        binding.btnSubmit.text = "Login"
        binding.tvTogglePrompt.text = "New here? Sign up →"
    }

    private fun switchToSignup() {
        isLoginMode = false
        binding.btnTabSignup.alpha = 1f
        binding.btnTabLogin.alpha = 0.45f
        binding.tilName.visibility = View.VISIBLE
        binding.tilPhone.visibility = View.VISIBLE
        binding.btnSubmit.text = "Create Account"
        binding.tvTogglePrompt.text = "Already have an account? Login →"
    }

    private fun setupButtons() {
        binding.tvTogglePrompt.setOnClickListener {
            if (isLoginMode) switchToSignup() else switchToLogin()
        }

        binding.btnSubmit.setOnClickListener {
            if (isLoginMode) attemptLogin() else attemptSignup()
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.tilEmail.error = "Enter your email"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email"
            return
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Enter your password"
            return
        }
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        // Simulate login — use email prefix as name
        val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        session.saveLogin(name, email)
        Toast.makeText(this, "Welcome back, $name!", Toast.LENGTH_SHORT).show()
        goToMain()
    }

    private fun attemptSignup() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (name.isEmpty()) { binding.tilName.error = "Enter your name"; return }
        if (email.isEmpty()) { binding.tilEmail.error = "Enter your email"; return }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter a valid email"; return
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"; return
        }

        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        session.saveLogin(name, email, phone)
        Toast.makeText(this, "Welcome, $name! Your journey begins.", Toast.LENGTH_SHORT).show()
        goToMain()
    }

    private fun goToMain() {
        startActivity(Intent(this, FortListActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
