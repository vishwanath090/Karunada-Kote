package com.karunadakote.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.karunadakote.data.local.SessionManager
import com.karunadakote.databinding.ActivityProfileBinding
import com.karunadakote.ui.auth.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var session: SessionManager
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadUserData()
        setupButtons()
        animateIn()
    }

    private fun animateIn() {
        listOf(binding.cardProfile, binding.cardStats, binding.cardActions)
            .forEachIndexed { i, card ->
                card.alpha = 0f
                card.translationY = 40f
                card.animate().alpha(1f).translationY(0f)
                    .setStartDelay(i * 120L).setDuration(400).start()
            }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Profile"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun loadUserData() {
        val name     = session.getUserName()
        val email    = session.getUserEmail()
        val phone    = session.getUserPhone()
        val joinDate = session.getJoinDate()

        // Avatar + header
        binding.tvUserInitial.text = name.first().uppercase()
        binding.tvUserName.text    = name
        binding.tvUserEmail.text   = email
        binding.tvJoinDate.text    = "Member since $joinDate"

        // Pre-fill edit fields
        binding.etName.setText(name)
        binding.etEmail.setText(email)
        binding.etPhone.setText(phone)

        // View-mode detail rows
        binding.tvDisplayName.text  = name
        binding.tvDisplayEmail.text = email
        binding.tvDisplayPhone.text = if (phone.isNotEmpty()) phone else "Not set"

        // Visited count — correct prefs file + key prefix
        val visitedPrefs = getSharedPreferences("karunada_kote_prefs", MODE_PRIVATE)
        val visitedCount = visitedPrefs.all.keys.count { it.startsWith("visited_fort_") }

        binding.tvVisitedCount.text   = visitedCount.toString()
        binding.tvRemainingCount.text = (30 - visitedCount).toString()
        binding.progressVisited.max      = 30
        binding.progressVisited.progress = visitedCount
        binding.tvProgressLabel.text = when {
            visitedCount == 0 -> "Start exploring Karnataka's forts!"
            visitedCount < 10 -> "Great start! Keep exploring."
            visitedCount < 25 -> "You're a seasoned explorer!"
            else              -> "Master of Karnataka's Heritage! 🏆"
        }
    }

    private fun setupButtons() {
        binding.btnEditSave.setOnClickListener {
            if (!isEditMode) enterEditMode() else saveProfile()
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    session.logout()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnExploreMore.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun enterEditMode() {
        isEditMode = true
        binding.btnEditSave.text         = "Save Changes"
        binding.layoutViewMode.visibility = View.GONE
        binding.layoutEditMode.visibility = View.VISIBLE
        binding.etName.requestFocus()
    }

    private fun saveProfile() {
        val newName  = binding.etName.text.toString().trim()
        val newEmail = binding.etEmail.text.toString().trim()
        val newPhone = binding.etPhone.text.toString().trim()

        if (newName.isEmpty()) {
            binding.tilEditName.error = "Name cannot be empty"; return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            binding.tilEditEmail.error = "Enter a valid email"; return
        }
        binding.tilEditName.error  = null
        binding.tilEditEmail.error = null

        session.saveLogin(newName, newEmail, newPhone)

        // Refresh display
        binding.tvUserName.text     = newName
        binding.tvUserEmail.text    = newEmail
        binding.tvUserInitial.text  = newName.first().uppercase()
        binding.tvDisplayName.text  = newName
        binding.tvDisplayEmail.text = newEmail
        binding.tvDisplayPhone.text = if (newPhone.isNotEmpty()) newPhone else "Not set"

        isEditMode = false
        binding.btnEditSave.text          = "Edit Profile"
        binding.layoutEditMode.visibility  = View.GONE
        binding.layoutViewMode.visibility  = View.VISIBLE

        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
    }
}