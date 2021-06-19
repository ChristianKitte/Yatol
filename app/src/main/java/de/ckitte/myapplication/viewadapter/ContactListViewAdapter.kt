package de.ckitte.myapplication.viewadapter

import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.databinding.FragmentContactListitemBinding
import de.ckitte.myapplication.model.EditToDoModel
import okio.Options

class ContactListViewAdapter(
    private val viewModel: EditToDoModel,
    private val contentResolver: ContentResolver?,
    private val packageManager: PackageManager?,
    private val parentFragment: Fragment
) :
    ListAdapter<ToDoContact, ContactListViewAdapter.ContactViewHolder>(ContactComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding =
            FragmentContactListitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ContactViewHolder(
            binding,
            viewModel,
            contentResolver,
            packageManager,
            parentFragment
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ContactViewHolder(
        private val binding: FragmentContactListitemBinding,
        private val viewModel: EditToDoModel,
        private val contentResolver: ContentResolver?,
        private val packageManager: PackageManager?,
        private val parentFragment: Fragment
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
                    callContact("05418603498", "Der Titel")
                }

                btnMail.setOnClickListener {
                    var title = "Kein Titel"
                    var anrede = "Hallo ${tvContact.text.toString()}"

                    viewModel.getCurrentToDoItem()?.let {
                        title = "${it.toDoTitle} am ${it.toDoDoUntil.toLocalDate()}"
                    }

                    sendEmail(
                        arrayOf<String>("chkitte@web.de"),
                        title, anrede, title
                    )
                }
            }
        }

        fun callContact(number: String, title: String) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${number}")
                putExtra(Intent.EXTRA_TITLE, title)
            }

            if (packageManager?.let { intent.resolveActivity(it) } != null) {
                parentFragment.startActivity(intent)
            }
        }

        fun sendEmail(addresses: Array<String>, subject: String, text: String, title: String) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                //z.B. "chkitte@web.de"
                //arrayOf<String>("chkitte@web.de")
                putExtra(Intent.EXTRA_EMAIL, addresses)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, text)
                putExtra(Intent.EXTRA_TITLE, title)
            }

            if (packageManager?.let { intent.resolveActivity(it) } != null) {
                parentFragment.startActivity(intent)
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