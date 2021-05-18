package de.ckitte.myapplication.surface

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import de.ckitte.myapplication.Model.EditToDoModel
import de.ckitte.myapplication.Model.ToDoListModel
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentEditTodoBinding
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import java.time.LocalDateTime

class EditToDo : Fragment(R.layout.fragment_edit_todo) {
    private lateinit var viewModel: EditToDoModel

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
        this.viewModel = toDoRepository?.let { EditToDoModel(it) }!!

        val _binding = FragmentEditTodoBinding.bind(view)
        val currentToDoItem = viewModel.getCurrentToDoItem()

        val apply = _binding.apply {
            currentToDoItem?.apply {
                etTitle.setText(toDoTitle)
                etDescription.setText(toDoDescription)
                etDoUntil.setText(toDoDoUntil.toString())
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

        _binding.btnSave.setOnClickListener {
            if (currentToDoItem != null) {
                _binding.apply {
                    currentToDoItem.apply {
                        toDoTitle = etTitle.text.toString()
                        toDoDescription = etDescription.text.toString()
                        val test = etDoUntil.text.toString()
                        toDoDoUntil = LocalDateTime.parse(etDoUntil.text.toString())
                        toDoIsDone = checkIsDone.isChecked
                        toDoIsFavourite = checkIsFavourite.isChecked
                        checkIsFavourite.isChecked = toDoIsFavourite

                        viewModel.updateToDoItem(currentToDoItem)
                    }
                }
            }

            it.findNavController().navigate(R.id.action_editToDo_to_toDoListFragment)
        }

        _binding.btnBack.setOnClickListener {
            it.findNavController().navigate(R.id.action_editToDo_to_toDoListFragment)
        }

        _binding.btnDelete.setOnClickListener {
            if (currentToDoItem != null) {
                viewModel.deleteToDoItem(currentToDoItem)
            }

            Snackbar.make(view, "Der Eintrag wurde gelöscht", Snackbar.LENGTH_LONG).apply {
                setAction("Abbruch") {
                    if (currentToDoItem != null) {
                        viewModel.addToDoItem(currentToDoItem)
                    }
                }
            }.show()

            it.findNavController().navigate(R.id.action_editToDo_to_toDoListFragment)
        }
    }
}
