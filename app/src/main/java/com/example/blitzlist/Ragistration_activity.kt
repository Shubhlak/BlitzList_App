package com.example.blitzlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.blitzlist.databinding.ActivityRagistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import kotlin.concurrent.thread

class Ragistration_activity : AppCompatActivity() {
    private lateinit var ragistrationBinding: ActivityRagistrationBinding
    private var uri: Uri? = null
    private lateinit var fbauth: FirebaseAuth
    private lateinit var storageRef: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ragistrationBinding = ActivityRagistrationBinding.inflate(layoutInflater)
        setContentView(ragistrationBinding.root)

        // Initialization of all the Firebase instances
        fbauth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()

        val image = ragistrationBinding.userimg
        val btnchoose = ragistrationBinding.btnChoseimg

        // Image picker and setter code
        val galleryImage = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                image.setImageURI(it)
                uri = it
            })
        btnchoose.setOnClickListener {
            galleryImage.launch("image/*")
        }

        // User registration
        ragistrationBinding.btnRegister.setOnClickListener {
            val progressBar = ragistrationBinding.progressbar
            val username = ragistrationBinding.edtusername.text.toString()
            val useremail = ragistrationBinding.edtuseremail.text.toString()
            val password = ragistrationBinding.edtpassword.text.toString()
            val confirmPass = ragistrationBinding.edtconfermpassword.text.toString()

            progressBar.visibility = View.VISIBLE // Show ProgressBar

            if (useremail.isEmpty()) {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            } else if (password != confirmPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            } else if (username.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            } else {
                // Create user with email and password
                fbauth.createUserWithEmailAndPassword(useremail, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = FirebaseAuth.getInstance().currentUser!!.uid

                        if (uri != null) {
                            // Upload image to Firebase Storage
                            thread {
                                storageRef.reference.child("Image_of_user").putFile(uri!!)
                                    .addOnSuccessListener { taskSnapshot ->
                                        taskSnapshot.metadata!!.reference!!.downloadUrl
                                            .addOnSuccessListener { uri ->
                                                val imageUrl = uri.toString()

                                                // Update Realtime Database
                                                val databaseReference = FirebaseDatabase.getInstance("https://blitzlist-app-default-rtdb.asia-southeast1.firebasedatabase.app")
                                                    .getReference("User") // Reference to the main User node

                                                // User data with username and userImages_Urls child nodes
                                                val userData = hashMapOf(
                                                    "username" to username,
                                                    "userImages_Urls" to imageUrl
                                                )

                                                databaseReference.child(userId).setValue(userData)
                                                    .addOnSuccessListener {
                                                        progressBar.visibility = View.GONE
                                                        Toast.makeText(this@Ragistration_activity, "Registration successful", Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(this@Ragistration_activity, Login_activity::class.java)
                                                        startActivity(intent)
                                                    }.addOnFailureListener { error ->
                                                        Toast.makeText(this@Ragistration_activity, error.toString(), Toast.LENGTH_LONG).show()
                                                        progressBar.visibility = View.GONE
                                                    }
                                            }
                                    }
                            }
                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@Ragistration_activity, "Image not selected", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Ragistration_activity, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }
}
