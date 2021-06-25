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
import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.databinding.FragmentContactListitemBinding
import de.ckitte.myapplication.model.EditToDoModel

/**
 *
 * @property viewModel EditToDoModel
 * @property contentResolver ContentResolver?
 * @property packageManager PackageManager?
 * @property parentFragment Fragment
 * @constructor
 */
class ContactListViewAdapter(
    /**
     *
     */
    private val viewModel: EditToDoModel,
    /**
     *
     */
    private val contentResolver: ContentResolver?,
    /**
     *
     */
    private val packageManager: PackageManager?,
    /**
     *
     */
    private val parentFragment: Fragment
) :
    ListAdapter<LocalToDoContact, ContactListViewAdapter.ContactViewHolder>(ContactComparator()) {

    /**
     *
     * @param parent ViewGroup
     * @param viewType Int
     * @return ContactViewHolder
     */
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

    /**
     *
     * @param holder ContactViewHolder
     * @param position Int
     */
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    /**
     *
     * @property binding FragmentContactListitemBinding
     * @property viewModel EditToDoModel
     * @property contentResolver ContentResolver?
     * @property packageManager PackageManager?
     * @property parentFragment Fragment
     * @property currentPhoneNumber String
     * @property currentEmail String
     * @constructor
     */
    class ContactViewHolder(
        private val binding: FragmentContactListitemBinding,
        private val viewModel: EditToDoModel,
        private val contentResolver: ContentResolver?,
        private val packageManager: PackageManager?,
        private val parentFragment: Fragment
    ) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         *
         */
        private lateinit var currentPhoneNumber: String

        /**
         *
         */
        private lateinit var currentEmail: String

        /**
         *
         * @param contact LocalToDoContact
         */
        fun bind(contact: LocalToDoContact) {
            binding.apply {

                if (contentResolver != null) {
                    val uri: Uri = Uri.parse(contact.toDoContactLocalUri)

                    tvContact.text = viewModel.getDisplayName(uri, contentResolver)
                    //tvContact.text = getDisplayNameByUri(uri, contentResolver!!)

                    currentPhoneNumber = viewModel.getPhoneNumber(uri, contentResolver)
                    if (currentPhoneNumber.isEmpty()) {
                        this.btnCall.isEnabled = false
                    }

                    currentEmail = viewModel.getEmailAdress(uri, contentResolver)
                    if (currentEmail.isEmpty()) {
                        this.btnMail.isEnabled = false
                    }
                } else {
                    tvContact.text = contact.toDoContactLocalUri
                }

                btnCall.setOnClickListener {
                    var title = "Kein Titel"

                    viewModel.getCurrentToDoItem()?.let {
                        title = "${it.toDoLocalTitle} am ${it.toDoLocalDoUntil.toLocalDate()}"
                    }

                    callContact(currentPhoneNumber, title)
                }

                btnMail.setOnClickListener {
                    var title = "Kein Titel"
                    val anrede = "Hallo ${tvContact.text.toString()}"

                    viewModel.getCurrentToDoItem()?.let {
                        title = "${it.toDoLocalTitle} am ${it.toDoLocalDoUntil.toLocalDate()}"
                    }

                    sendEmail(
                        arrayOf<String>(currentEmail),
                        title,
                        anrede,
                        title
                    )
                }
            }
        }

        /**
         *
         * @param number String
         * @param title String
         */
        fun callContact(number: String, title: String) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${number}")
                putExtra(Intent.EXTRA_TITLE, title)
            }

            if (packageManager?.let { intent.resolveActivity(it) } != null) {
                parentFragment.startActivity(intent)
            }
        }

        /**
         *
         * @param addresses Array<String>
         * @param subject String
         * @param text String
         * @param title String
         */
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

    /**
     *
     */
    class ContactComparator : DiffUtil.ItemCallback<LocalToDoContact>() {
        /**
         *
         * @param oldItem LocalToDoContact
         * @param newItem LocalToDoContact
         * @return Boolean
         */
        override fun areItemsTheSame(oldItem: LocalToDoContact, newItem: LocalToDoContact): Boolean {
            return oldItem === newItem
        }

        /**
         *
         * @param oldItem LocalToDoContact
         * @param newItem LocalToDoContact
         * @return Boolean
         */
        override fun areContentsTheSame(oldItem: LocalToDoContact, newItem: LocalToDoContact): Boolean {
            return oldItem.toDoContactLocalId == newItem.toDoContactLocalId
        }
    }
}