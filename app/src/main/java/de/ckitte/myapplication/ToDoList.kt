package de.ckitte.myapplication

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import de.ckitte.myapplication.Adapter.ToDoListViewAdapter
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import de.ckitte.myapplication.main.ToDoApplication
import de.ckitte.myapplication.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.ArrayList

class ToDoList : Fragment() {
    // View Binding
    private var _binding: FragmentTodoListBinding? = null
    private val binding get() = _binding!!

    // Create a viewModel
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = MainViewModel(ToDoRepository(ToDoApplication().repository))
        _binding = de.ckitte.myapplication.databinding.FragmentTodoListBinding.inflate(inflater, container, false)

        var toDoListViewAdapter = ToDoListViewAdapter()
        _binding?.rvtodoitems?.adapter = toDoListViewAdapter

        toDoListViewAdapter.setNewUser(refreshUserList())
        //toDoListViewAdapter.notifyItemInserted(0)

        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupClickListeners()
    }


    fun refreshUserList(): ArrayList<ToDo> {
        var toDoList: ArrayList<ToDo> = ArrayList()
        toDoList.add(
            ToDo(
                0,
                "Sample 1",
                "toDo Sample 1",
                false,
                true,
                LocalDateTime.now(),
                ToDoRepository.defaultGroup
            )
        )

        return toDoList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    private fun setupClickListeners() {
        binding.btnDoSomething.setOnClickListener {
            GlobalScope.launch {
                viewModel.getUpdatedText()

                var Listenausgabe = ""
                for (listItem in viewModel.test()) {
                    Listenausgabe += listItem.toString() + "\n"
                }

                withContext(Dispatchers.Main) {
                    binding.tvShowSomething.text = Listenausgabe
                }
            }
        }
    }
    */
}