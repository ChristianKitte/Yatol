package de.ckitte.myapplication.surface

import android.graphics.BlendMode
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.ckitte.myapplication.model.ToDoListModel
import de.ckitte.myapplication.R
import de.ckitte.myapplication.util.ListSort
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import de.ckitte.myapplication.login.LoginProvider
import de.ckitte.myapplication.util.ConnectionLiveData
import de.ckitte.myapplication.viewadapter.ToDoListViewAdapter
import kotlinx.coroutines.*

/**
 * Die Fensterklasse für die Oberfläche des Hauptfenster
 * @property viewModel ToDoListModel Das zuständige ViewModel
 * @property toDoListViewAdapter ToDoListViewAdapter Der Adapter für die Listenansicht der ToDos
 * @property _binding FragmentTodoListBinding Die generierte Bindingklasse zur Layout Ressource
 * @property ItemTouchHelperCallback SimpleCallback Hilfsklasse zum Behandeln von Wischbewegungen
 */
class ToDoList : Fragment(R.layout.fragment_todo_list) {
    private lateinit var viewModel: ToDoListModel
    private lateinit var toDoListViewAdapter: ToDoListViewAdapter
    private lateinit var _binding: FragmentTodoListBinding

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
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
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

        this.viewModel = toDoRepository?.let { ToDoListModel(it) }!!

        toDoListViewAdapter = ToDoListViewAdapter(viewModel)

        this.viewModel.toDos.observe(viewLifecycleOwner) {
            toDoListViewAdapter.submitList(it)
        }

        _binding = FragmentTodoListBinding.bind(view)
        _binding.apply {
            rvtodoitems.apply {
                adapter = toDoListViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true) //optimierung
            }

            //Den mittleren Menüpunkt disablen
            menuBottomNavigation.menu.getItem(2).isEnabled = false
            menuBottomNavigation.setOnNavigationItemSelectedListener {
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
                        if (ConnectionLiveData.isConnected) {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.refreshDatabase()
                            }
                        } else {
                            showToast("Aktuell ist keine Netzverbindung vorhanden!")
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

            fabAdd.setOnClickListener {
                viewModel.iniNewToDoItem()
                it.findNavController().navigate(R.id.action_toDoList_to_editToDo)
            }
        }

        /*
        val parentActivity = this.activity
        if (parentActivity != null) {
            val connectionLiveData = ConnectionLiveData(parentActivity)

            connectionLiveData.observe(parentActivity, {
                _binding.menuBottomNavigation.menu.getItem(1).isEnabled = it
            })
        }
        */

        ItemTouchHelper(ItemTouchHelperCallback).apply {
            attachToRecyclerView(_binding.rvtodoitems)
        }
    }

    //region Movefunktionalität

    /**
     * Callbackfunktion zum behandeln von Wischbewegungen auf der Liste der ToDos
     */
    private val ItemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
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
         * Überschreibt onSwiped und löscht das ToDoItem bei einem Wischen nach links
         * @param viewHolder ViewHolder Der ViewHolder der ToDoKontakte
         * @param direction Int Die Wischbewegung
         */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val currentItemIndex = viewHolder.adapterPosition
            val currentItem = toDoListViewAdapter.currentList[currentItemIndex]

            if (direction == ItemTouchHelper.LEFT) {
                confirmToDoDelete(currentItem)
            }
        }
    }

    //endregion Bestätigungsdialoge und Logik

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
                    // Das Item ist bereits aus der View entfernt worden und muss wieder eingelesen werden
                    toDoListViewAdapter.notifyDataSetChanged()
                }
                .setPositiveButton("Löschen") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.deleteToDoItem(currentLokalToDo)
                        withContext(Dispatchers.Main) {
                            toDoListViewAdapter.notifyDataSetChanged()
                        }
                    }
                }
                .show()
        }
    }

    //endregion

    //region Hilfsfunktionen

    /**
     * Hilfsfunktion zum vereinfachten Anzeigen eines Toasts
     * @param text String Der anzuzeigende Text
     */
    private fun showToast(text: String) {
        Toast.makeText(
            this.activity,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    //endregion
}