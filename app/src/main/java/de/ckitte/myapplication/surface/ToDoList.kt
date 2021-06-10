package de.ckitte.myapplication.surface

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import de.ckitte.myapplication.model.ToDoListModel
import de.ckitte.myapplication.R
import de.ckitte.myapplication.util.ListSort
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import de.ckitte.myapplication.login.LoginProvider
import de.ckitte.myapplication.util.ConnectionLiveData
import de.ckitte.myapplication.viewadapter.ToDoListViewAdapter
import kotlinx.coroutines.*

class ToDoList : Fragment(R.layout.fragment_todo_list) {
    private lateinit var viewModel: ToDoListModel
    private lateinit var toDoListViewAdapter: ToDoListViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mi_sort_date -> {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.changeSortOrder(ListSort.DateThenImportance)
                }

                true
            }
            R.id.mi_sort_favourite -> {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.changeSortOrder(ListSort.ImportanceThenDate)
                }

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
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
        this.viewModel = toDoRepository?.let { ToDoListModel(it) }!!

        val _binding = FragmentTodoListBinding.bind(view)
        //val toDoListViewAdapter = ToDoListViewAdapter(viewModel)
        toDoListViewAdapter = ToDoListViewAdapter(viewModel)

        _binding.apply {
            rvtodoitems.apply {
                adapter = toDoListViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true) //optimierung
            }
        }

        _binding.menuBottomNavigation.menu.getItem(2).isEnabled = false

        _binding.fabAdd.setOnClickListener {
            viewModel.iniNewToDoItem()
            it.findNavController().navigate(R.id.action_toDoList_to_editToDo)
        }

        _binding.menuBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miClose -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        LoginProvider.LogOut()
                        withContext(Dispatchers.Main) {
                            this@ToDoList.activity?.finishAndRemoveTask()
                        }
                    }
                }
                R.id.miRefresh -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.refreshDatabase()
                    }
                }
                R.id.mi_sort_date -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.changeSortOrder(ListSort.DateThenImportance)
                    }
                }
                R.id.mi_sort_favourite -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.changeSortOrder(ListSort.ImportanceThenDate)
                    }
                }
            }

            true
        }

        val ItemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val currentItemIndex = viewHolder.adapterPosition
                val currentItem = toDoListViewAdapter.currentList[currentItemIndex]
                viewModel.deleteToDoItem(currentItem)

                Snackbar.make(view, "Der Eintrag wurde gelöscht", Snackbar.LENGTH_LONG).apply {
                    setAction("Abbruch") {
                        viewModel.addToDoItem(currentItem)
                    }
                }.show()
            }
        }

        ItemTouchHelper(ItemTouchHelperCallback).apply {
            attachToRecyclerView(_binding.rvtodoitems)
        }

        this.viewModel.toDos.observe(viewLifecycleOwner) {
            //Achtung: Ich habe lange gesucht. Problem: Zunächst TextView Höhe auf 0 gewesen
            //dann das Fragment selbst nicht auf den Inhalt angepasst.
            //Sehr böse Falle und nicht leicht aufzuspüren...
            toDoListViewAdapter.submitList(it)
        }
    }
}