package de.ckitte.myapplication.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import de.ckitte.myapplication.databinding.FragmentTodoListitemBinding
import java.time.LocalDateTime
import java.util.ArrayList
import de.ckitte.myapplication.Adapter.ToDoListViewAdapter.ToDoViewHolder

//ListADapter neuer als RecyclerView.Adapter. Keine Implementirung mti getItemCount
//Es muss keine Liste gehalten werden. Arbeitet mit SubmitList aus dem Model
//https://blog.usejournal.com/why-you-should-be-using-the-new-and-improved-listadapter-in-android-17a2ab7ca644
//https://developer.android.com/codelabs/android-room-with-a-view-kotlin#11
class ToDoListViewAdapter : ListAdapter<ToDo, ToDoViewHolder>(ToDoComparator()) {
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

        //private val toDoItemView: TextView = itemView.findViewById(R.id.tvContent)

        fun bind(toDo: ToDo) {
            binding.apply {
                tvContent.text = toDo.toDoTitle
            }
            //toDoItemView.text = text
        }

        /*
        companion object {
            fun create(parent: ViewGroup): ToDoViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_todo_listitem, parent, false)
                return ToDoViewHolder(view)
            }
        }
        */

    }

    class ToDoComparator : DiffUtil.ItemCallback<ToDo>() {
        override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem.toDoId == newItem.toDoId
        }
    }
}