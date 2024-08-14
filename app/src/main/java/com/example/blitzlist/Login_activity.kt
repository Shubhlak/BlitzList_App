package com.example.blitzlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blitzlist.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login_activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loginActivityBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        if (isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        loginActivityBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginActivityBinding.root)
        auth = FirebaseAuth.getInstance()

        loginActivityBinding.tvRegister.setOnClickListener {
            val intent = Intent(this@Login_activity, Ragistration_activity::class.java)
            startActivity(intent)
        }

        loginActivityBinding.btnlogin.setOnClickListener {
            val username = loginActivityBinding.edtusername.text.toString()
            val password = loginActivityBinding.edtpassword.text.toString()

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            saveLoginState(true)
                            val intent = Intent(this@Login_activity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun saveLoginState(isLoggedIn: Boolean) {
        val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", isLoggedIn)
            apply()
        }
    }

    private fun isLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("isLoggedIn", false)
    }
}
