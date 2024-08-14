package com.example.blitzlist.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


// a data base need three things and they are

// 1. Entity -> Table structure -> how ur data is stored

@Entity(tableName = "notes_table")
data class Todo (
    @PrimaryKey(autoGenerate = true)
    val id:Int =0,

    var isMarkedDone:Boolean=false,

    val title:String,

    val desc:String,

    val date:Date       // --> ye gfg walw ne bataya hai bc //
//    val date: String // Ensure the date field is a String to store formatted date


)


// in a data base the data is stored only in this form that is  --> Integer,Text,Char,Boolean . So to store a custom object ( date in our case ) we need to convert that object into one of the accepted forms of db