package com.example.blitzlist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Todo::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class ListDatabase:RoomDatabase(){

    abstract fun todoDao():TodoDao
}