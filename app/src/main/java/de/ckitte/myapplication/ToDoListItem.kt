package de.ckitte.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import de.ckitte.myapplication.databinding.FragmentTodoListitemBinding
import de.ckitte.myapplication.main.ToDoApplication
import de.ckitte.myapplication.viewmodel.MainViewModel

class ToDoListItem : Fragment() {
    // View Binding
    private var _binding: FragmentTodoListitemBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodoListitemBinding.inflate(inflater, container, false)

        val view = binding.root
        return view
    }
}