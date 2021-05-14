package de.ckitte.myapplication.viewadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentTodoListitemBinding
import de.ckitte.myapplication.surface.ToDoList
import de.ckitte.myapplication.viewadapter.ToDoListViewAdapter.ToDoViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                tvTitle.text = toDo.toDoTitle
                tvDescription.text = toDo.toDoDescription

                checkIsDone.isChecked = toDo.toDoIsDone
                checkIsFavourite.isChecked = toDo.toDoIsFavourite

                tvDoUntil.text = toDo.toDoDoUntil.toString()

                fabEdit.setOnClickListener {
                    Toast.makeText(
                        root.context,
                        "Den aktuellen Eintrag editieren ( ${toDo.toDoId} )",
                        Toast.LENGTH_SHORT
                    ).show()

                    //var x = Navigation.findNavController(R.id.activityLayout)
                }

                fabDelete.setOnClickListener {
                    GlobalScope.launch {
                        val db = ToDoDatabase.getInstance(root.context, GlobalScope).toToDao
                        ToDoRepository(db).deleteToDo(toDo.toDoId)
                    }

                    Toast.makeText(
                        root.context,
                        "Der Eintrag ${toDo.toDoTitle} wurde gelöscht ( ${toDo.toDoId} )",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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