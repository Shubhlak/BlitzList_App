package com.example.blitzlist.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


// 2. the 2 important this for a database is a dao
// DAO --> Data Access Object  --> This helps u access the database without writting the extra code for it

@Dao
interface TodoDao {
    // this interface helps us perform CRUD operation on the database
    // CRUD --> Creat | Read | Update | Delete


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodo(todo: Todo)


    @Query("select*from notes_table")
    fun fetchAllTodos() :MutableList<Todo>


    //serch functionallity
    // below code is being replaced by gpt
//    @Query("select * from notes_table where notes_table.`desc`like :serchQuery or notes_table.title like :serchQuery ")
//    fun fetchSerchedTodo(serchQuery: String):MutableList<Todo>
    @Query("SELECT * FROM notes_table WHERE `desc` LIKE :searchQuery OR title LIKE :searchQuery")
    fun fetchSearchedTodo(searchQuery: String): MutableList<Todo>




    // this is for deleting the todos
    @Query("DELETE FROM notes_table")
    fun deleteAllTodos()


    // here we are using just two functions that is update and insert and delete , we are not using the  update
}