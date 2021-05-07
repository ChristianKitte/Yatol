package de.ckitte.myapplication

import android.app.Application
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.ckitte.myapplication.Adapter.ToDoListViewAdapter
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import de.ckitte.myapplication.main.ToDoApplication
import de.ckitte.myapplication.viewmodel.MainViewModel
//import de.ckitte.myapplication.viewmodel.WordViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope

class ToDoList : Fragment(R.layout.fragment_todo_list) {
    // Add RecyclerView member
    //private var recyclerView: RecyclerView? = null

    // View Binding
    //private var _binding: FragmentTodoListBinding? = null
    //private val binding get() = _binding!!

    // Create a viewModel
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewModel = MainViewModel(ToDoRepository(ToDoApplication().repository))
        val dao = parentFragment?.let {
            ToDoDatabase.getInstance(view.context,
                it.lifecycleScope).toToDao
        }
        this.viewModel = MainViewModel(dao)

        //Wenn ohne Konstruktor_binding = FragmentTodoListBinding.inflate(inflater, container, false)
        //wenn R.layout.fragment_todo_list im Konstruktor, da es schon inflatet ist, eine Abkürzung... !
        val _binding = FragmentTodoListBinding.bind(view)
        val toDoListViewAdapter = ToDoListViewAdapter()

        _binding.apply {
            this!!.rvtodoitems.apply {
                adapter=toDoListViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true) //optimierung
            }
        }

        this.viewModel.toDos?.observe(viewLifecycleOwner){
            //Achtung: Ich habe lange gesucht. Problem:
            //zunächst Höhe Text auf 0 gesetzt
            //dann Die Umgebung, den Frame nicht auf den Inhalt angepasst.
            //sehr böse Falle und nciht leicht aufzuspüren

            toDoListViewAdapter.submitList(it)//==> es werden die sieben Items übergeben !!!

            //toDoListViewAdapter.submitList(ArrayList(it))

            //toDoListViewAdapter.submitList(it?.toMutableList())
            //toDoListViewAdapter.notifyDataSetChanged()
        }
        //https://www.youtube.com/watch?v=eLbgQYMGMm4&list=PLrnPJCHvNZuCfAe7QK2BoMPkv2TGM_b0E&index=5
        //https://stackoverflow.com/questions/49726385/listadapter-not-updating-item-in-recyclerview
    }
/*
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = binding.root

        //setupClickListeners()
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvtodoitems)

        recyclerView.adapter = toDoListViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        //_binding?.rvtodoitems?.adapter = toDoListViewAdapter
        //_binding?.rvtodoitems?.layoutManager=LinearLayout(this)


        var x: String = ""
        GlobalScope.launch {
            viewModel.getUpdatedText()
            withContext(Dispatchers.Main) {
                //x = viewModel.x
                //val xx=0
            }
        }


        return view
    }
*/
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