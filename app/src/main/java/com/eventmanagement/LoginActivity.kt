package com.eventmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eventmanagement.data.FirebaseRepository
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView

    private val repository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // Check if already logged in
        if (repository.getCurrentUser() != null) {
            navigateToHome()
            return
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validation
            when {
                email.isEmpty() -> {
                    etEmail.error = "Email is required"
                    etEmail.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    etPassword.error = "Password is required"
                    etPassword.requestFocus()
                    return@setOnClickListener
                }
                !email.endsWith("@college.edu") -> {
                    etEmail.error = "Please use college email (@college.edu)"
                    etEmail.requestFocus()
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    etPassword.error = "Password must be at least 6 characters"
                    etPassword.requestFocus()
                    return@setOnClickListener
                }
            }

            loginUser(email, password)
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Password reset coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser(email: String, password: String) {
        btnLogin.isEnabled = false
        btnLogin.text = "Logging in..."

        lifecycleScope.launch {
            val result = repository.login(email, password)

            if (result.isSuccess) {
                Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            } else {
                val error = result.exceptionOrNull()?.message ?: "Login failed"
                Toast.makeText(this@LoginActivity, error, Toast.LENGTH_LONG).show()
                btnLogin.isEnabled = true
                btnLogin.text = "Login"
            }
        }
    }

    private fun navigateToHome() {
        lifecycleScope.launch {
            val user = repository.getCurrentUser() ?: return@launch
            val userDataResult = repository.getUserData(user.uid)

            if (userDataResult.isSuccess) {
                val userData = userDataResult.getOrNull()
                val intent = if (userData?.role == "admin") {
                    Intent(this@LoginActivity, AdminActivity::class.java)
                } else {
                    Intent(this@LoginActivity, MainActivity::class.java)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Failed to load user data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}