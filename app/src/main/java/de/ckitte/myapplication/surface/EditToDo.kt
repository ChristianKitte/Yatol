package de.ckitte.myapplication.surface

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import de.ckitte.myapplication.model.EditToDoModel
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentEditTodoBinding
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

    val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
          var x=it
        //bitmap: Bitmap? ->
        // Do something with the Bitmap, if present
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
            var i = Intent(Intent.ACTION_PICK)
            i.type=ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE


            val x=takePicture.launch()

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
