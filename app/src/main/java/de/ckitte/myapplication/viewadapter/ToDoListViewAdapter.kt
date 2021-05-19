package de.ckitte.myapplication.viewadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RestrictTo
import androidx.annotation.WorkerThread
import androidx.navigation.NavDirections
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.Model.ToDoListModel
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
import java.time.LocalDateTime

class ToDoListViewAdapter(private val viewModel: ToDoListModel) :
    ListAdapter<ToDoItem, ToDoViewHolder>(ToDoComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding =
            FragmentTodoListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ToDoViewHolder(
        private val binding: FragmentTodoListitemBinding,
        private var viewModel: ToDoListModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(toDo: ToDoItem) {
            binding.apply {
                tvTitle.text = toDo.toDoTitle
                tvDescription.text = toDo.toDoDescription

                checkIsDone.isChecked = toDo.toDoIsDone
                checkIsFavourite.isChecked = toDo.toDoIsFavourite

                tvDoUntil.text = getDoUntilString(toDo.toDoDoUntil)

                fabEdit.setOnClickListener {
                    viewModel.setCurrentToDoItem(toDo)
                    it.findNavController().navigate(R.id.action_toDoListFragment_to_editToDo)
                }

                checkIsDone.setOnClickListener {
                    toDo.toDoIsDone = checkIsDone.isChecked
                    viewModel.updateToDoItem(toDo)
                }

                checkIsFavourite.setOnClickListener {
                    toDo.toDoIsFavourite = checkIsFavourite.isChecked
                    viewModel.updateToDoItem(toDo)
                }
            }
        }

        private fun getDoUntilString(dateTime: LocalDateTime): String {
            val currentDayString = dateTime.dayOfMonth.toString().padStart(2, '0')
            val currentMonthString = dateTime.monthValue.toString().padStart(2, '0')
            val currentYearString = dateTime.year.toString().padStart(4, '0')
            val currentHourString = dateTime.hour.toString().padStart(2, '0')
            val currentMinuteString = dateTime.minute.toString().padStart(2, '0')

            return "Am $currentDayString.$currentMonthString.$currentYearString um $currentHourString:$currentMinuteString Uhr"
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