package de.ckitte.myapplication.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.databinding.FragmentTodoListitemBinding
import de.ckitte.myapplication.Adapter.ToDoListViewAdapter.ToDoViewHolder

//ListADapter neuer als RecyclerView.Adapter. Keine Implementirung mti getItemCount
//Es muss keine Liste gehalten werden. Arbeitet mit SubmitList aus dem Model
//https://blog.usejournal.com/why-you-should-be-using-the-new-and-improved-listadapter-in-android-17a2ab7ca644
//https://developer.android.com/codelabs/android-room-with-a-view-kotlin#11
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

        //private val toDoItemView: TextView = itemView.findViewById(R.id.tvContent)

        fun bind(toDo: ToDoItem) {
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

    class ToDoComparator : DiffUtil.ItemCallback<ToDoItem>() {
        override fun areItemsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
            return oldItem.toDoId == newItem.toDoId
        }
    }
}