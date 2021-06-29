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
         *
         * checkPermission(Manifest.permission.READ_CONTACTS, 100)
         * checkPermission(Manifest.permission.CALL_PHONE, 110)
         * checkPermission(Manifest.permission.SEND_SMS, 120)
         *
         * @param contact LocalToDoContact Der zu bindende [LocalToDoContact]
         */
        fun bind(contact: LocalToDoContact) {
            binding.apply {

                if (contentResolver != null) {
                    val uri: Uri = Uri.parse(contact.toDoContactLocalUri)

                    tvContact.text = viewModel.getDisplayName(uri, contentResolver)

                    //ich gehe hier davon aus, das heute jedes Telefon - auch ein Festnetz - eine
                    //SMS empfangen kann
                    currentPhoneNumber = viewModel.getPhoneNumber(uri, contentResolver)
                    if (currentPhoneNumber.isEmpty()) {
                        this.btnCall.isEnabled = false
                        this.btnSms.isEnabled = false
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

                btnSms.setOnClickListener {
                    var title = "Kein Titel"
                    val anrede = "Hallo ${tvContact.text}"

                    viewModel.getCurrentToDoItem()?.let {
                        title = "${it.toDoLocalTitle} am ${it.toDoLocalDoUntil.toLocalDate()}"
                    }

                    sendSms(
                        currentPhoneNumber,
                        "Betreff: ${title}. $anrede"
                    )
                }

                btnMail.setOnClickListener {
                    var title = "Kein Titel"
                    val anrede = "Hallo ${tvContact.text}"

                    viewModel.getCurrentToDoItem()?.let {
                        title = "${it.toDoLocalTitle} am ${it.toDoLocalDoUntil.toLocalDate()}"
                    }

                    sendEmail(
                        arrayOf(currentEmail),
                        title,
                        anrede,
                        title
                    )
                }
            }
        }

        /**
         * Initialisiert einen Anruf (kein direct call, sondern nur die Vorbelegung zum Einleiten
         * des Anrufes). Dies ist aus meiner Sicht sinnvoll, da der Nutzer so mehr Freiheiten hat.
         * @param number String Die zu verwendende Telefonnummer
         * @param title String der zu verwendende Titel
         */
        private fun callContact(number: String, title: String) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${number}")
                putExtra(Intent.EXTRA_TITLE, title)
            }

            if (packageManager?.let { intent.resolveActivity(it) } != null) {
                parentFragment.startActivity(intent)
            }
        }

        /**
         * Initialisiert eine SMS. Hierbei wird die hierfür vorgesehene Activity
         * direkt geöffnet. So ist es möglich, den Text noch anzupassen und eine
         * vorhergehende Unterhaltung nochmals einzusehen.
         * https://www.tutorialspoint.com/how-to-send-a-sms-using-smsmanager-in-dual-sim-mobile-in-android-using-kotlin
         * https://www.androidcookbook.info/application-development/sending-sms-and-mms-from-your-application-using-intents-and-the-native-client.html
         * @param number String Die zu verwendende Telefonnummer
         * @param message String Der Text der SMS
         */
        fun sendSms(number: String, message: String) {

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("sms:${number}")
                putExtra("sms_body", message)
            }

            if (packageManager?.let { intent.resolveActivity(it) } != null) {
                parentFragment.startActivity(intent)
            }

            //Der hier zu sehende Code sendet direkt eine SMS, gibt aber keine
            //Rückmeldung vom System und somit keine eindeutige Bestätigung:
            //val smsManager = SmsManager.getDefault()
            //smsManager.sendTextMessage(number, null, message, null, null)
        }

        /**
         * Initialisiert ein eMailversand
         * @param addresses Array<String> Die zu verwendenen Adressen
         * @param subject String Der Betreff der eMail
         * @param text String Der Text der eMail
         * @param title String Der Titel der eMail
         */
        private fun sendEmail(addresses: Array<String>, subject: String, text: String, title: String) {
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