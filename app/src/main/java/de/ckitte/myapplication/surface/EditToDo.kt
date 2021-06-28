package de.ckitte.myapplication.surface

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.databinding.FragmentEditTodoBinding
import de.ckitte.myapplication.model.EditToDoModel
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.util.EmailUtil
import de.ckitte.myapplication.viewadapter.ContactListViewAdapter
import kotlinx.coroutines.*
import java.time.LocalDateTime

/**
 * Die Fensterklasse für die Edit Oberfläche
 * @property _viewModel EditToDoModel Das zuständige ViewModel
 * @property contactListViewAdapter ContactListViewAdapter Der Adapter für die Listenansicht der ToDoContacts
 * @property _binding FragmentEditTodoBinding Die generierte Bindingklasse zur Layout Ressource
 * @property cal android.icu.util.Calendar Eine Instanz der Android Kalender Komponente
 * @property currentDay Int Der aktuelle Tag
 * @property currentMonth Int Der aktuelle Monat
 * @property currentYear Int Das aktuelle Jahr
 * @property currentHour Int Die aktuelle Stunde
 * @property currentMinute Int Die aktuelle Minute
 * @property ItemTouchHelperCallback SimpleCallback Hilfsklasse zum Behandeln von Wischbewegungen
 */
class EditToDo : Fragment(R.layout.fragment_edit_todo),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var _viewModel: EditToDoModel
    private lateinit var contactListViewAdapter: ContactListViewAdapter
    private lateinit var _binding: FragmentEditTodoBinding

    private val cal = Calendar.getInstance()
    private var currentDay = cal.get(Calendar.DAY_OF_MONTH)
    private var currentMonth = cal.get(Calendar.MONTH)
    private var currentYear = cal.get(Calendar.YEAR)
    private var currentHour = cal.get(Calendar.HOUR)
    private var currentMinute = cal.get(Calendar.MINUTE)

    /**
     * Überschreibt die onCreateView Methode und erstellt eine neue Klasse auf Basis der Ressource
     * @param inflater LayoutInflater Das übergebene LayoutInflater Objekt
     * @param container ViewGroup? Der übergebene Container
     * @param savedInstanceState Bundle? Das übergebene Bundle Objekt
     * @return View?
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_todo, container, false)
    }

    /**
     * Überschreibt die onViewCreate Methode und initialisiert die Klasse und setzt alle notwendigen EventHandler
     * @param view View Die Übergebene View
     * @param savedInstanceState Bundle? Das übergebene Bundle Objekt
     */
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = parentFragment?.let {
            ToDoDatabase.getInstance(
                view.context
            ).toToDao
        }

        val toDoRepository = dao?.let { ToDoRepository(it) }

        _viewModel = toDoRepository?.let { EditToDoModel(it) }!!

        contactListViewAdapter = ContactListViewAdapter(
            _viewModel, activity?.contentResolver,
            this.activity?.packageManager, this
        )

        this._viewModel.toDoContacts.observe(viewLifecycleOwner) {
            contactListViewAdapter.submitList(it)
        }

        val currentToDoItem = _viewModel.getCurrentToDoItem()

        _binding = FragmentEditTodoBinding.bind(view)

        setUpCalender() // benötigt _binding!
        _binding.apply {
            rvContacts.apply {
                adapter = contactListViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true) //optimierung
            }

            currentToDoItem?.apply {
                setCalender(toDoLocalDoUntil)

                etTitle.setText(toDoLocalTitle)
                etDescription.setText(toDoLocalDescription)
                tvDoUntil.text = getDoUntilString()
                checkIsDone.isChecked = toDoLocalIsDone
                checkIsFavourite.isChecked = toDoLocalIsFavourite
            }

            btnSave.isEnabled =
                (etTitle.length() > 0) && (etDescription.length() > 0)

            etTitle.addTextChangedListener {
                validateForm()
            }

            etDescription.addTextChangedListener {
                validateForm()
            }

            btnSave.setOnClickListener {
                currentToDoItem?.let {
                    saveCurrentToDo(it)
                }
            }

            btnBack.setOnClickListener {
                if (currentToDoItem != null) {
                    _viewModel.rollbackToDoContacts()
                }

                it.findNavController().navigate(R.id.action_editToDo_to_toDoListFragment)
            }

            btnContacts.setOnClickListener {
                currentToDoItem?.let {
                    when (it.toDoLocalId) {
                        0 -> confirmSaveBeforeAddContact(it)
                        else -> selectContact()
                    }
                }
            }

            btnDelete.setOnClickListener {
                currentToDoItem?.let {
                    confirmToDoDelete(it)
                }
            }
        }

        validateForm()

        ItemTouchHelper(ItemTouchHelperCallback).apply {
            attachToRecyclerView(_binding.rvContacts)
        }
    }

    //region Hinzufügen eines Kontaktes

    /**
     * Launcher als Ergebnis der Registrierung für ein Activity mit Result. Die annonyme Callbackfunktion
     * ruft die Methode [addToDoContact] auf
     */
    var resultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent? = result.data

                if (intent != null) {
                    val uri = intent.data
                    uri?.let {
                        addToDoContact(uri)
                    }
                }
            }
        }

    /**
     * Ruft den KontaktPicker von Android auf und nutz hierfür den Launcher [resultLauncher]
     */
    fun selectContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        resultLauncher.launch(intent)
    }

    /**
     * Fügt die übergebene URI dem ToDoItem als neuen Kontakt hinzu
     * @param uri Uri Die gültige lokale URI eines Kontaktes
     */
    fun addToDoContact(uri: Uri) {

        val newContact = _viewModel.getNewToDoContact()
        val currentToDoItem = _viewModel.getCurrentToDoItem()

        if (currentToDoItem != null) {
            newContact.apply {
                toDoContactLocalId = 0
                toDoContactRemoteId = ""
                toDoContactLocalUri = uri.toString()
                toDoLocalId = currentToDoItem.toDoLocalId.toLong()
                toDoRemoteId = currentToDoItem.toDoRemoteId
            }

            _viewModel.addToDoContact(newContact)
            contactListViewAdapter.notifyDataSetChanged()
        }
    }

    //endregion

    //region Movefunktionalität

    /**
     * Callbackfunktion zum behandeln von Wischbewegungen auf der Liste der ToDoKontakte
     */
    val ItemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT
    ) {
        /**
         *  Überschreibt on Move und gibt True zurück
         * @param recyclerView RecyclerView Die zugrundeliegende RecyclerView
         * @param viewHolder ViewHolder Der ViewHolder der View
         * @param target ViewHolder Das Ziel der Bewegung
         * @return Boolean True
         */
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        /**
         * Überschreibt onSwiped und löscht den ToDoContact bei einem Wischen nach links
         * @param viewHolder ViewHolder Der ViewHolder der ToDoKontakte
         * @param direction Int Die Wischbewegung
         */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val currentItemIndex = viewHolder.adapterPosition
            val currentItem = contactListViewAdapter.currentList[currentItemIndex]

            if (direction == ItemTouchHelper.LEFT) {
                confirmToDoContactDelete(currentItem)
            }
        }
    }

    //endregion

    //region Bestätigungsdialoge und Logik

    /**
     * Bestätigungsdialog für den Hinweis zur Speicherung des ToDoItems vor dem Hinzufügen von Kontakten
     * @param currentLokalToDo LocalToDo Das betreffende [LocalToDo] Element
     */
    fun confirmSaveBeforeAddContact(currentLokalToDo: LocalToDo) {
        this.context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Eintrag speichern?")
                .setMessage("Bevor ein Kontakt hinzugefügt werden kann, muss der Eintrag gespeichert werden.")

                .setNegativeButton("Abbrechen") { _, _ ->
                    // Respond to negative button press
                }
                .setPositiveButton("Speichern") { _, _ ->
                    saveCurrentToDo(currentLokalToDo)
                    selectContact()
                }
                .show()
        }
    }

    /**
     * Bestätigungsdialog für das Löschen des aktuellen ToDoContacts
     * @param currentLokalToDoContact LocalToDoContact Das betreffende [LocalToDoContact] Element
     */
    fun confirmToDoContactDelete(currentLokalToDoContact: LocalToDoContact) {
        this.context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Kontakt löschen?")
                .setMessage("Soll der Kontakt wirklich gelöscht werden?")

                .setNegativeButton("Abbrechen") { _, _ ->
                    // Das Item ist bereits aus der View entfernt worden und muss wieder eingelesen werden
                    contactListViewAdapter.notifyDataSetChanged()
                }
                .setPositiveButton("Löschen") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        _viewModel.deleteToDoContact(currentLokalToDoContact)
                        withContext(Dispatchers.Main) {
                            contactListViewAdapter.notifyDataSetChanged()
                        }
                    }
                }
                .show()
        }
    }

    /**
     * Bestätigungsdialog für das Löschen des aktuellen ToDoItems
     * @param currentLokalToDo LocalToDo Das betreffende [LocalToDo] Element
     */
    fun confirmToDoDelete(currentLokalToDo: LocalToDo) {
        this.context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Eintrag löschen?")
                .setMessage("Soll der Eintrag wirklich gelöscht werden?")

                .setNegativeButton("Abbrechen") { _, _ ->
                    // Respond to negative button press
                }
                .setPositiveButton("Löschen") { _, _ ->
                    _viewModel.deleteToDoItem(currentLokalToDo)
                    _binding.root.findNavController()
                        .navigate(R.id.action_editToDo_to_toDoListFragment)
                }
                .show()
        }
    }

    //endregion

    //region Datum und Zeitangaben

    /**
     * Defniert einen Handler für den Aufruf eines DatePicker auf Basis der aktuellen Datumswerte.
     */
    private fun setUpCalender() {
        _binding.tvDoUntil.setOnClickListener {
            // month is zero based!
            DatePickerDialog(it.context, this, currentYear, currentMonth - 1, currentDay).show()
        }
    }

    /**
     * Rückgabefunktion eines DatePickers. Ruft nach Speicherung der erhaltenen Datumswerte als aktuelle
     * Datumsangaben einen TimePicker auf und initialisiert diesen auf Basis der aktuell gehaltenen Zeitangabe.
     * @param view DatePicker Der DateTimePicker
     * @param year Int Das gewählte Jahr
     * @param month Int Der gewählte Monat
     * @param dayOfMonth Int Der gewählte Tag des Monats
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        currentDay = dayOfMonth
        currentMonth = month + 1 // month is zero based!
        currentYear = year

        _binding.tvDoUntil.text = getDoUntilString()

        TimePickerDialog(this.view?.context, this, currentHour, currentMinute, true).show()
    }

    /**
     * Rückgabefunktion eines TimePickers. Aktualisiert nach Speicherung der erhaltenen Zeitwerte als aktuelle
     * Zeitangabe den Zeichenstring, welcher den aktuellen Termin des ToDoItems als Text anzeigt.
     * @param view TimePicker Der TimePicker
     * @param hourOfDay Int Die gewählte Stunde
     * @param minute Int Die gewählte Minute
     */
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        currentHour = hourOfDay
        currentMinute = minute

        _binding.tvDoUntil.text = getDoUntilString()
    }

    //endregion

    //region Hilfsfunktionen

    /**
     * Erzeugt einen formatierten String mit Datum und Uhrzeit für die
     * direkte Anzeige auf Basis der aktuellen lokalen Variablen
     * @return String Der Anzeigestring des Datums und der Uhrzeit
     */
    private fun getDoUntilString(): String {
        val currentDayString = currentDay.toString().padStart(2, '0')
        val currentMonthString = currentMonth.toString().padStart(2, '0')
        val currentYearString = currentYear.toString().padStart(4, '0')
        val currentHourString = currentHour.toString().padStart(2, '0')
        val currentMinuteString = currentMinute.toString().padStart(2, '0')

        return "Am $currentDayString.$currentMonthString.$currentYearString um $currentHourString:$currentMinuteString Uhr"
    }

    /**
     * Erzeugt eine LocalDateTime Instanz auf Basis der aktuellen lokalen Variablen
     * @param dateTime LocalDateTime
     */
    private fun setCalender(dateTime: LocalDateTime) {
        dateTime.apply {
            currentDay = dayOfMonth
            currentMonth = monthValue
            currentYear = year
            currentHour = hour
            currentMinute = minute
        }
    }

    /**
     * Speichert das übergebene ToDoItem
     * @param currentLokalToDo LocalToDo Das betreffende [LocalToDo] Element
     */
    fun saveCurrentToDo(currentLokalToDo: LocalToDo) {
        currentLokalToDo.let {
            _binding.apply {
                currentLokalToDo.apply {
                    toDoLocalTitle = etTitle.text.toString()
                    toDoLocalDescription = etDescription.text.toString()

                    toDoLocalDoUntil = LocalDateTime.of(
                        currentYear,
                        currentMonth,
                        currentDay,
                        currentHour,
                        currentMinute
                    )

                    toDoLocalIsDone = checkIsDone.isChecked
                    toDoLocalIsFavourite = checkIsFavourite.isChecked

                    _viewModel.updateToDoItem(it)

                    Toast.makeText(context, "Eintrag wurde gespeichert", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    /**
     * Prüft formale Kriterien der Eingabe des Titels und der Beschreibung. Wenn alle Voraussetzungen erfüllt
     * sind, wird der Speicher Button enabled.
     */
    private fun validateForm() {
        _binding.apply {
            btnSave.isEnabled = etTitle.length() > 0 && etDescription.length() > 0
            btnContacts.isEnabled = etTitle.length() > 0 && etDescription.length() > 0
        }
    }

    //endregion
}
