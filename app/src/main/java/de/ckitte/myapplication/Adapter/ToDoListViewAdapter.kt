package de.ckitte.myapplication.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import de.ckitte.myapplication.databinding.FragmentTodoListitemBinding
import java.time.LocalDateTime
import java.util.ArrayList

class ToDoListViewAdapter : RecyclerView.Adapter<ToDoListViewAdapter.ViewHolder>() {
    var toDoList: List<ToDo> = ArrayList<ToDo>()


    // Neue Users anzeigen...
    fun setNewUser(toDoList: ArrayList<ToDo>) {
        this.toDoList = toDoList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvContent: TextView = itemView.findViewById(R.id.tvContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_todo_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvContent.text = toDoList[position].toString()
    }

    override fun getItemCount(): Int {
        return this.toDoList.count()
    }
}