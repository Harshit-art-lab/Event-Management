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

class SignUpActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etUsn: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var tvLogin: TextView

    private val repository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Sign Up"

        // Initialize views
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etUsn = findViewById(R.id.etUsn)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        tvLogin = findViewById(R.id.tvLogin)

        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val usn = etUsn.text.toString().trim().uppercase()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            // Validation
            when {
                name.isEmpty() -> {
                    etName.error = "Name is required"
                    etName.requestFocus()
                }
                email.isEmpty() -> {
                    etEmail.error = "Email is required"
                    etEmail.requestFocus()
                }
                !email.endsWith("@college.edu") -> {
                    etEmail.error = "Please use college email (@college.edu)"
                    etEmail.requestFocus()
                }
                usn.isEmpty() -> {
                    etUsn.error = "USN is required"
                    etUsn.requestFocus()
                }
                usn.length < 10 -> {
                    etUsn.error = "USN must be at least 10 characters"
                    etUsn.requestFocus()
                }
                password.isEmpty() -> {
                    etPassword.error = "Password is required"
                    etPassword.requestFocus()
                }
                password.length < 6 -> {
                    etPassword.error = "Password must be at least 6 characters"
                    etPassword.requestFocus()
                }
                password != confirmPassword -> {
                    etConfirmPassword.error = "Passwords do not match"
                    etConfirmPassword.requestFocus()
                }
                else -> {
                    signUpUser(name, email, usn, password)
                }
            }
        }

        tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun signUpUser(name: String, email: String, usn: String, password: String) {
        btnSignUp.isEnabled = false
        btnSignUp.text = "Creating account..."

        lifecycleScope.launch {
            val result = repository.signUp(name, email, usn, password)

            if (result.isSuccess) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Account created successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                // Seed initial events (only runs once)
                repository.seedInitialEvents()

                // Navigate to home
                val userData = repository.getUserData(result.getOrNull()!!.uid)
                if (userData.isSuccess) {
                    val userRole = userData.getOrNull()?.role
                    val intent = if (userRole == "admin") {
                        Intent(this@SignUpActivity, AdminActivity::class.java)
                    } else {
                        Intent(this@SignUpActivity, MainActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Sign up failed"
                Toast.makeText(this@SignUpActivity, error, Toast.LENGTH_LONG).show()
                btnSignUp.isEnabled = true
                btnSignUp.text = "Sign Up"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}