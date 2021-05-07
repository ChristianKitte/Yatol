package de.ckitte.myapplication.surface

//import android.app.Fragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import de.ckitte.myapplication.viewadapter.ToDoListViewAdapter
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import de.ckitte.myapplication.Model.ToDoListModel
import de.ckitte.myapplication.database.repository.ToDoRepository

class ToDoList : Fragment(R.layout.fragment_todo_list) {
    private lateinit var viewModel: ToDoListModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = parentFragment?.let {
            ToDoDatabase.getInstance(
                view.context,
                it.lifecycleScope
            ).toToDao
        }

        val toDoRepository = dao?.let { ToDoRepository(it) }
        this.viewModel = toDoRepository?.let { ToDoListModel(it) }!!

        // Wenn im Konstruktor nichts weiter angegeben ist:
        // _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        // Wenn R.layout.fragment_todo_list im Konstruktor übergeben
        // wird, ist der folgende Code mögliche:

        val _binding = FragmentTodoListBinding.bind(view)
        val toDoListViewAdapter = ToDoListViewAdapter()

        _binding.apply {
            this.rvtodoitems.apply {
                adapter = toDoListViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true) //optimierung
            }
        }

        this.viewModel.toDos.observe(viewLifecycleOwner) {
            //Achtung: Ich habe lange gesucht. Problem: Zunächst TextView Höhe auf 0 gewesen
            //dann das Fragment selbst nicht auf den Inhalt angepasst.
            //Sehr böse Falle und nicht leicht aufzuspüren...

            toDoListViewAdapter.submitList(it)
        }
    }
}