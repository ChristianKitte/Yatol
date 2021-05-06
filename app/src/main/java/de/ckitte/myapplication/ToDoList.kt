package de.ckitte.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.Adapter.ToDoListViewAdapter
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import de.ckitte.myapplication.main.ToDoApplication
import de.ckitte.myapplication.viewmodel.MainViewModel
import de.ckitte.myapplication.viewmodel.WordViewModelFactory

class ToDoList : Fragment() {
    // Add RecyclerView member
    //private var recyclerView: RecyclerView? = null

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
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)

        val view = binding.root

        //setupClickListeners()
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvtodoitems)
        val toDoListViewAdapter = ToDoListViewAdapter()
        recyclerView.adapter = toDoListViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        //_binding?.rvtodoitems?.adapter = toDoListViewAdapter
        //_binding?.rvtodoitems?.layoutManager=LinearLayout(this)

        return view
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