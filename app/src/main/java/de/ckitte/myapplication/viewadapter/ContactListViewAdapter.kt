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
 * ListView Adapter für die Kontaktliste eines ToDos
 * @property viewModel EditToDoModel Das zugehörige ViewModel
 * @property contentResolver ContentResolver? Ein gültiger ContentResolver Objekt
 * @property packageManager PackageManager? Ein gültiger PackageManager
 * @property parentFragment Fragment Eine Referenz auf das zugehörige Fragment
 * @constructor
 */
class ContactListViewAdapter(
    private val viewModel: EditToDoModel,
    private val contentResolver: ContentResolver?,
    private val packageManager: PackageManager?,
    private val parentFragment: Fragment
) :
    ListAdapter<LocalToDoContact, ContactListViewAdapter.ContactViewHolder>(ContactComparator()) {

    /**
     * Wird bei der Erzeugung eines neuen ViewHolders aufgerufen
     * @param parent ViewGroup Die übergebene ViewGruppe
     * @param viewType Int Der übergebene ViewTyp
     * @return ContactViewHolder Eine neue Instanz von [ContactViewHolder]
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
     * Wird bei der Bindung von Daten und ViewHolder aufgerufen
     * @param holder ContactViewHolder Der übergebene View Holder vom Typ [ContactViewHolder]
     * @param position Int Die Position des zu bindenden Elements
     */
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    /**
     * Der [ContactViewHolder] des Adapters. Implementiert die Logik der visuelle Darstellung des Kontaktes auf Basis
     * der hinterlegten Ressource
     * @property binding FragmentContactListitemBinding Ein Binding Objekt der zu verwendenen Ressource
     * @property viewModel EditToDoModel Das zu verwendende ViewModel
     * @property contentResolver ContentResolver? Ein gültes ContentResolver Objekt
     * @property packageManager PackageManager? Ein gültiger PacketManager
     * @property parentFragment Fragment Das zugrunde liegende Fragment
     * @property currentPhoneNumber String Die aktuelle Telefonnummer des Kontaktes
     * @property currentEmail String Die aktuelle eMail Adresse des Kontaktes
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

        private lateinit var currentPhoneNumber: String
        private lateinit var currentEmail: String

        /**
         * Wird zum Binden des ToDoContacts aufgerufen
         * @param contact LocalToDoContact Der zu bindende [LocalToDoContact]
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
         * Initialisiert einen Anruf (kein direct call, sondern nur die Vorbelegung zum Einleiten des Anrufes)
         * @param number String Die zu verwendende Telefonnummer
         * @param title String der zu verwendende Titel
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
         * Initialisiert ein eMailversand
         * @param addresses Array<String> Die zu verwendenen Adressen
         * @param subject String Der Betreff der eMail
         * @param text String Der Text der eMail
         * @param title String Der Titel der eMail
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
     * Ein Vergleichsobjekt für [LocalToDoContact]
     */
    class ContactComparator : DiffUtil.ItemCallback<LocalToDoContact>() {
        /**
         * Zwei Elemente sind gleich, wenn deren Instanzen gleich sind
         * @param oldItem LocalToDoContact Das alte Element
         * @param newItem LocalToDoContact Das neue Element
         * @return Boolean True, wenn sie identisch sind, sonst False
         */
        override fun areItemsTheSame(
            oldItem: LocalToDoContact,
            newItem: LocalToDoContact
        ): Boolean {
            return oldItem === newItem
        }

        /**
         * Zwei Elemente sind identisch, wenn deren lokale URI übereinstimmen
         * @param oldItem LocalToDoContact Das alte Element
         * @param newItem LocalToDoContact Das neue Element
         * @return Boolean True, wenn sie gleich sind, sonst False
         */
        override fun areContentsTheSame(
            oldItem: LocalToDoContact,
            newItem: LocalToDoContact
        ): Boolean {
            return oldItem.toDoContactLocalUri == newItem.toDoContactLocalUri
        }
    }
}