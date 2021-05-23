package de.ckitte.myapplication.surface

import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import de.ckitte.myapplication.Model.AddToDoModel
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentAddTodoBinding
import java.time.*


class AddToDo : Fragment(R.layout.fragment_add_todo) {
    private lateinit var _viewModel: AddToDoModel
    private lateinit var _binding: FragmentAddTodoBinding

    private lateinit var newToDoItem: ToDoItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_todo, container, false)
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

        _viewModel = toDoRepository?.let { AddToDoModel(it) }!!
        _binding = FragmentAddTodoBinding.bind(view)

        newToDoItem = _viewModel.getNewToDoItem()

        _binding.apply {
            newToDoItem.apply {
                etTitle.setText(toDoTitle)
                etDescription.setText(toDoDescription)
                tvDoUntil.text = getDoUntilString(toDoDoUntil)
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
                        val x = 0
                        btnSave.isEnabled = (etTitle.length() > 0) && (etDescription.length() > 0)
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
        )

        _binding.btnSave.isEnabled = false

        setUpCalender()

        _binding.btnSave.setOnClickListener {
            _viewModel.addToDoItem(this.newToDoItem)
            it.findNavController().navigate(R.id.action_addToDo_to_toDoListFragment)
        }

        _binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_addToDo_to_toDoListFragment)
        }
    }

    //https://material.io/components/date-pickers/android#using-date-pickers
    //https://www.nuomiphp.com/eplan/en/256164.html
    private fun setUpCalender() {
        _binding.tvDoUntil.setOnClickListener {
            // month is zero based!
            val openAt: Long =
                newToDoItem.toDoDoUntil.atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant()
                    .toEpochMilli()

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(openAt)
                .build()

            datePicker.addOnPositiveButtonClickListener {
                val cal: Calendar = Calendar.getInstance(android.icu.util.TimeZone.getDefault())
                cal.timeInMillis = datePicker.selection!!

                val localDate = LocalDate.of(
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH).plus(1), cal.get(Calendar.DAY_OF_MONTH)
                )

                newToDoItem.toDoDoUntil =
                    LocalDateTime.of(localDate, newToDoItem.toDoDoUntil.toLocalTime())

                _binding.apply {
                    newToDoItem.apply {
                        etTitle.setText(toDoTitle)
                        etDescription.setText(toDoDescription)
                        tvDoUntil.text = getDoUntilString(toDoDoUntil)
                        checkIsDone.isChecked = toDoIsDone
                        checkIsFavourite.isChecked = toDoIsFavourite
                    }
                }

                datePicker.dismissAllowingStateLoss()
                setUpTime()
            }

            datePicker.show(this.parentFragmentManager, "Showtext")
        }
    }

    private fun setUpTime() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(0)
            .setMinute(0)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val localTime = LocalTime.of(
                timePicker.hour, timePicker.minute
            )

            newToDoItem.toDoDoUntil =
                LocalDateTime.of(newToDoItem.toDoDoUntil.toLocalDate(), localTime)
            _binding.tvDoUntil.text = getDoUntilString(newToDoItem.toDoDoUntil)

            timePicker.dismissAllowingStateLoss()
        }

        timePicker.show(this.parentFragmentManager, "Showtext")
    }

    private fun getDoUntilString(dateTime: LocalDateTime): String {
        val currentDayString = dateTime.dayOfMonth.toString().padStart(2, '0')
        val currentMonthString = dateTime.monthValue.toString().padStart(2, '0')
        val currentYearString = dateTime.year.toString().padStart(4, '0')
        val currentHourString = dateTime.hour.toString().padStart(2, '0')
        val currentMinuteString = dateTime.minute.toString().padStart(2, '0')

        return "Am $currentDayString.$currentMonthString.$currentYearString um $currentHourString:$currentMinuteString Uhr"
    }
}