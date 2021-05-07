package de.ckitte.myapplication.viewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.databinding.FragmentTodoListitemBinding
import de.ckitte.myapplication.viewadapter.ToDoListViewAdapter.ToDoViewHolder

class ToDoListViewAdapter : ListAdapter<ToDoItem, ToDoViewHolder>(ToDoComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding =
            FragmentTodoListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ToDoViewHolder(private val binding: FragmentTodoListitemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(toDo: ToDoItem) {
            binding.apply {
                //tvContent.text = toDo.toDoTitle
                tvContent.text = toDo.toDoDoUntil.toString()
            }
        }
    }

    class ToDoComparator : DiffUtil.ItemCallback<ToDoItem>() {
        override fun areItemsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
            return oldItem.toDoId == newItem.toDoId
        }
    }
}