package de.ckitte.myapplication.viewadapter

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.databinding.FragmentContactListitemBinding
import de.ckitte.myapplication.model.EditToDoModel
import de.ckitte.myapplication.model.ToDoListModel
import de.ckitte.myapplication.util.getDisplayNameByUri
import java.util.jar.Manifest
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

class ContactListViewAdapter(
    private val viewModel: EditToDoModel,
    private val contentResolver: ContentResolver?
) :
    ListAdapter<ToDoContact, ContactListViewAdapter.ContactViewHolder>(ContactComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding =
            FragmentContactListitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ContactViewHolder(binding, viewModel, contentResolver)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ContactViewHolder(
        private val binding: FragmentContactListitemBinding,
        private var viewModel: EditToDoModel,
        private var contentResolver: ContentResolver?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: ToDoContact) {
            binding.apply {

                if (contentResolver != null) {
                    val uri: Uri = Uri.parse(contact.toDoContactHostId)

                    tvContact.text = viewModel.getDisplayName(uri, contentResolver!!)
                    //tvContact.text = getDisplayNameByUri(uri, contentResolver!!)
                } else {
                    tvContact.text = contact.toDoContactHostId
                }

                btnCall.setOnClickListener {
                    Snackbar.make(
                        root,
                        "Call !",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                btnMail.setOnClickListener {
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