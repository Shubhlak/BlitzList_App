package com.example.blitzlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blitzlist.databinding.TodoItemBinding
import com.example.blitzlist.db.Todo

class TodoListViewholder(
    private val itemBinding: TodoItemBinding,
    private val listener: TodostateChangedlistner
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bindData(todo: Todo, position: Int) {
        itemBinding.cdTodoitem.isChecked = todo.isMarkedDone
        itemBinding.tvitemtitle.text = todo.title
        itemBinding.tvitemdesc.text = todo.desc
        itemBinding.tvitemdate.text = todo.date.toString()

        // Set the onCheckedChangeListener for the CheckBox
        itemBinding.cdTodoitem.setOnCheckedChangeListener { _, _ ->
            listener.onCheckStateChanged(position)
        }
    }
}

class TodolistAdaptor(
    var listofTodos: MutableList<Todo>,
    private val listener: TodostateChangedlistner
) : RecyclerView.Adapter<TodoListViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewholder {
        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoListViewholder(binding, listener)
    }

    override fun getItemCount() = listofTodos.size

    override fun onBindViewHolder(holder: TodoListViewholder, position: Int) {
        holder.bindData(listofTodos[position], position)
    }

    // this updateds the todolists
    fun updatedata(newList: MutableList<Todo>) {
        listofTodos = newList
        notifyDataSetChanged()
    }

    // this adds an item
    fun addnewitem(newTodo: Todo) {
        listofTodos.add(0, newTodo)
        notifyItemInserted(0)
    }


    // this deleates all the todos form the todolist
    fun clearAllItems() {
        listofTodos.clear()
        notifyDataSetChanged()
    }


    // this moves the
    fun moveItemToEnd(position: Int) {
        val todo = listofTodos.removeAt(position)
        todo.isMarkedDone = true
        listofTodos.add(todo)
        notifyItemMoved(position, listofTodos.size - 1)
    }
}

interface TodostateChangedlistner {
    fun onCheckStateChanged(position: Int)
}
