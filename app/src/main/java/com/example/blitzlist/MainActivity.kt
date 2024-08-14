package com.example.blitzlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.blitzlist.databinding.ActivityMainBinding
import com.example.blitzlist.databinding.BottomSheetBinding
import com.example.blitzlist.databinding.NavHeaderBinding
import com.example.blitzlist.db.ListDatabase
import com.example.blitzlist.db.Todo
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), TodostateChangedlistner {
    private lateinit var binding: ActivityMainBinding
    private lateinit var binding1:NavHeaderBinding
    private lateinit var database: ListDatabase
    private lateinit var adapter: TodolistAdaptor
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var databaseReference: DatabaseReference
//    private lateinit var ivFirebase:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding1 = NavHeaderBinding.bind(binding.navView.getHeaderView(0))
        setContentView(binding.root)






        // initializing the realtime db instance
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        databaseReference = FirebaseDatabase.getInstance("https://blitzlist-app-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("User").child(userId)


        // fetching the user data form this function
        fetchUserData()

        //        databaseReference = FirebaseDatabase.getInstance().getReference("userImages").child(userId) ===> this is old code

        // this is for fetching the user image form the realtime data base and also for loading the user name and email form the rdb











        // Initialize the adapter and pass 'this' as the listener
        adapter = TodolistAdaptor(mutableListOf(), this)
        binding.Rview.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        binding.Rview.adapter = adapter

        binding.fabaddtodo.setOnClickListener {  // this works for the bottomsheet
            showBottomSheet()
        }

        // Create a Random to-do, push this in the database
        thread { // using threads so that this does not interrupt the UI and runs in the background
            database = Room.databaseBuilder(this@MainActivity, ListDatabase::class.java, "todoListDB").build()
            val readwhatswritten = database.todoDao().fetchAllTodos()
            adapter.updatedata(readwhatswritten)
        }

        // Implementation of the navigation view
        val navigationView = binding.navView
        val drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // this is for navigation view
        // and its implimentation
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> Toast.makeText(this@MainActivity, "Reverting back to home", Toast.LENGTH_SHORT).show()
                R.id.navprofile -> Toast.makeText(this@MainActivity, "Under development", Toast.LENGTH_SHORT).show()
                R.id.navitem_settings -> Toast.makeText(this@MainActivity, "Under development", Toast.LENGTH_SHORT).show()
                R.id.itemnothingspecial -> Toast.makeText(this@MainActivity, "Reverting back to home", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> {
                    logout()
                    val intent = Intent(this@MainActivity, Login_activity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        binding.ivhamburger.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }




        // implimentation of the serchview and serch functionality

        // the below code is not working so new code was added

//        binding.searchView.setOnSearchClickListener {
//           thread {
//               val serchedElemet = database.todoDao().fetchSerchedTodo(binding.searchView.query.toString())
//               adapter.updatedata(serchedElemet)
//           }
//        }



        // the below code is new and is by chat gpt
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchTodos(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchTodos(it)
                }
                return true
            }
        })





        binding.ivdelete.setOnClickListener {
            deleteAllTodos()
        }
    }  // this is where the onCreates ENds XXXX









    // this is the part of the logout activity , this function logs out the user form the main activity

    private fun logout() {
        val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("isLoggedIn", false)
            apply()
        }
    }




// this is the part of the serch functionality

    private fun searchTodos(query: String) {
        thread {
            val searchedElement = database.todoDao().fetchSearchedTodo("%$query%")
            runOnUiThread {
                adapter.updatedata(searchedElement)
            }
        }
    }


    // this is the part of the navigation view
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }




    // this for delete funcnality
    private fun deleteAllTodos() {
        thread {
            database.todoDao().deleteAllTodos()
            runOnUiThread {
                adapter.clearAllItems()
            }
        }
    }



    // this part of the code displays the bottom sheet when we click the Floating action button
    //
    fun showBottomSheet() {
        val bottomSheet = BottomSheetBinding.inflate(layoutInflater)  // this line binds the UI to the Kotlin file
        val dialog = BottomSheetDialog(this)  // this line adds the functionality of the dialog box that appears from the bottom, to our UI
        dialog.setContentView(bottomSheet.root)

        bottomSheet.btnsave.setOnClickListener {
            if (bottomSheet.edtxtile.text.isNullOrEmpty()) {
                bottomSheet.edtxtile.error = "Title cannot be empty"
                return@setOnClickListener  // this takes us out of the listener so nothing is implemented after button click
            }
            if (bottomSheet.edtxdecs.text.isNullOrEmpty()) {
                bottomSheet.edtxdecs.error = "Please add some description"
                return@setOnClickListener
            }
            val newTodo = Todo(
                title = bottomSheet.edtxtile.text.toString(),
                desc = bottomSheet.edtxdecs.text.toString(),
                date = Date(System.currentTimeMillis())
            )
            adapter.addnewitem(newTodo)
            thread {
                database.todoDao().insertTodo(newTodo)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    // Implement the onCheckStateChanged method from the TodostateChangedlistner interface
    override fun onCheckStateChanged(position: Int) {
        adapter.moveItemToEnd(position)
    }






    // this part is to fetch the user data form the data base and to display it in the main activity
    private fun fetchUserData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("username").getValue(String::class.java)
                val userImageUrl = snapshot.child("userImages_Urls").getValue(String::class.java)

                userName?.let {
                    binding1.navhedusername.text = it
                }
                userImageUrl?.let {
                    Glide.with(this@MainActivity).load(it).into(binding1.userimage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
