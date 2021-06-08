package de.ckitte.myapplication.surface

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.databinding.FragmentEditTodoBinding
import de.ckitte.myapplication.model.EditToDoModel
import de.ckitte.myapplication.repository.ToDoRepository
import java.time.LocalDateTime

class EditToDo : Fragment(R.layout.fragment_edit_todo), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var _viewModel: EditToDoModel
    private lateinit var _binding: FragmentEditTodoBinding

    private val cal = Calendar.getInstance()
    private var currentDay = cal.get(Calendar.DAY_OF_MONTH)
    private var currentMonth = cal.get(Calendar.MONTH)
    private var currentYear = cal.get(Calendar.YEAR)
    private var currentHour = cal.get(Calendar.HOUR)
    private var currentMinute = cal.get(Calendar.MINUTE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //LoaderManager.getInstance(this).initLoader(0,null,this)
        return inflater.inflate(R.layout.fragment_edit_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = parentFragment?.let {
            ToDoDatabase.getInstance(
                view.context,
                it.lifecycleScope
            ).toToDao
        }

        val toDoRepository = dao?.let { ToDoRepository(it) }

        _viewModel = toDoRepository?.let { EditToDoModel(it) }!!
        _binding = FragmentEditTodoBinding.bind(view)

        val currentToDoItem = _viewModel.getCurrentToDoItem()

        _binding.apply {
            currentToDoItem?.apply {
                setCalender(toDoDoUntil)

                etTitle.setText(toDoTitle)
                etDescription.setText(toDoDescription)
                tvDoUntil.text = getDoUntilString()
                checkIsDone.isChecked = toDoIsDone
                checkIsFavourite.isChecked = toDoIsFavourite
            }
        }

        _binding.etTitle.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    _binding.apply {
                        btnSave.isEnabled = (etTitle.length() > 0) && (etDescription.length() > 0)
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
        )

        _binding.etDescription.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    _binding.apply {
                        btnSave.isEnabled = (etTitle.length() > 0) && (etDescription.length() > 0)
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
        )

        setUpCalender()

        _binding.btnSave.setOnClickListener {
            if (currentToDoItem != null) {
                _binding.apply {
                    currentToDoItem.apply {
                        toDoTitle = etTitle.text.toString()
                        toDoDescription = etDescription.text.toString()

                        toDoDoUntil = LocalDateTime.of(
                            currentYear,
                            currentMonth,
                            currentDay,
                            currentHour,
                            currentMinute
                        )

                        toDoIsDone = checkIsDone.isChecked
                        toDoIsFavourite = checkIsFavourite.isChecked

                        _viewModel.updateToDoItem(currentToDoItem)
                    }
                }
            }

            it.findNavController().navigate(R.id.action_editToDo_to_toDoListFragment)
        }

        _binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_editToDo_to_toDoListFragment)
        }

        _binding.btnContacts.setOnClickListener {
            selectContact()

            //it.findNavController().navigate(R.id.action_editToDo_to_contactsFragment)
        }

        _binding.btnDelete.setOnClickListener {
            if (currentToDoItem != null) {
                _viewModel.deleteToDoItem(currentToDoItem)
            }

            Snackbar.make(view, "Der Eintrag wurde gelöscht", Snackbar.LENGTH_LONG).apply {
                setAction("Abbruch") {
                    if (currentToDoItem != null) {
                        _viewModel.addToDoItem(currentToDoItem)
                    }
                }
            }.show()

            it.findNavController().navigate(R.id.action_editToDo_to_toDoListFragment)
        }
    }


    // https://cketti.de/2020/09/03/avoid-intent-resolveactivity/
    // https://www.tutorialguruji.com/android/onactivityresult-method-is-deprecated-what-is-the-alternative/amp/

    // https://www.programmersought.com/article/97174656608/
    // https://www.programmersought.com/article/67577517589/

    // https://code.tutsplus.com/tutorials/android-essentials-using-the-contact-picker--mobile-2017

    fun selectContact() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
        }

        val intent2 = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

        try {
            resultLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            // Display some error message
        }
    }

    var resultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val intent: Intent? = result.data

                if (intent != null) {
                    intent.dataString?.let { Log.d("Contact", it) }
                    processContactData(Uri.parse(intent.dataString))

                    var contactName: String? = null
                    var uri = intent.data

                    if (uri != null) {
                        val cr = activity?.contentResolver
                        cr?.let {
                            // querying contact data store
                            val cursor: Cursor? =
                                it.query(
                                    uri,
                                    null,
                                    null,
                                    null,
                                    null
                                )

                            cursor?.let {
                                if (it.moveToFirst()) {
                                    val x =
                                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                                    x?.let { Log.d("Contact", x) }
                                }
                            }
                        }


                        //contactName?.let { Log.d("Contact", it) }
                    }
                }
            }
        }


    fun processContactData(intent: Uri?) {
        // get the contact id from the Uri
        val id: String? = intent?.getLastPathSegment()
        id?.let { Log.d("Contact", it) }

        val newContact = _viewModel.getNewToDoContact()
        val currentToDoItem = _viewModel.getCurrentToDoItem()

        if (newContact != null && currentToDoItem != null && id != null) {
            newContact.apply {
                toDoContactId = 0
                toDoContactRemoteId = id
                toDoContactHostId = "reserve"
                toDoItemId = currentToDoItem.toDoId.toLong()
                toDoItemRemoteId = currentToDoItem.toDoRemoteId
            }

            _viewModel.addToDoContact(newContact)
        }
    }

    private fun setUpCalender() {
        _binding.tvDoUntil.setOnClickListener {
            // month is zero based!
            DatePickerDialog(it.context, this, currentYear, currentMonth - 1, currentDay).show()
        }
    }

    private fun getDoUntilString(): String {
        val currentDayString = currentDay.toString().padStart(2, '0')
        val currentMonthString = currentMonth.toString().padStart(2, '0')
        val currentYearString = currentYear.toString().padStart(4, '0')
        val currentHourString = currentHour.toString().padStart(2, '0')
        val currentMinuteString = currentMinute.toString().padStart(2, '0')

        return "Am $currentDayString.$currentMonthString.$currentYearString um $currentHourString:$currentMinuteString Uhr"
    }

    private fun setCalender(dateTime: LocalDateTime) {
        dateTime.apply {
            currentDay = dayOfMonth
            currentMonth = monthValue
            currentYear = year
            currentHour = hour
            currentMinute = minute
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        currentDay = dayOfMonth
        currentMonth = month + 1 // month is zero based!
        currentYear = year

        _binding.tvDoUntil.text = getDoUntilString()

        TimePickerDialog(this.view?.context, this, currentHour, currentMinute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        currentHour = hourOfDay
        currentMinute = minute

        _binding.tvDoUntil.text = getDoUntilString()
    }
}
