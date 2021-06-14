package de.ckitte.myapplication.viewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.databinding.FragmentContactListitemBinding
import de.ckitte.myapplication.model.EditToDoModel
import de.ckitte.myapplication.model.ToDoListModel

class ContactListViewAdapter(private val viewModel: EditToDoModel) :
    ListAdapter<ToDoContact, ContactListViewAdapter.ContactViewHolder>(ContactComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding =
            FragmentContactListitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ContactViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ContactViewHolder(
        private val binding: FragmentContactListitemBinding,
        private var viewModel: EditToDoModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: ToDoContact) {
            binding.apply {
                tvContact.text = contact.toDoContactHostId

                fabCall.setOnClickListener {
                    Snackbar.make(
                        root,
                        "Call !",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                fabMail.setOnClickListener {
                    Snackbar.make(
                        root,
                        "Mail !",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    class ContactComparator : DiffUtil.ItemCallback<ToDoContact>() {
        override fun areItemsTheSame(oldItem: ToDoContact, newItem: ToDoContact): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ToDoContact, newItem: ToDoContact): Boolean {
            return oldItem.toDoContactId == newItem.toDoContactId
        }
    }
}