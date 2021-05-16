package de.ckitte.myapplication.surface

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import de.ckitte.myapplication.Model.EditToDoModel
import de.ckitte.myapplication.Model.ToDoListModel
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentEditTodoBinding
import de.ckitte.myapplication.databinding.FragmentTodoListBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditToDo.newInstance] factory method to
 * create an instance of this fragment.
 */
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

        _binding.apply {
            tvTest.setText(currentToDoItem?.toDoTitle)
        }

        _binding.button.setOnClickListener {
            if (currentToDoItem != null) {
                currentToDoItem.toDoTitle = _binding.tvTest.text.toString()

                viewModel.updateToDoItem(currentToDoItem)
            }

            it.findNavController().navigate(R.id.action_editToDo_to_toDoListFragment)
        }
    }
}