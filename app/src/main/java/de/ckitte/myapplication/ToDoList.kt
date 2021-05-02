package de.ckitte.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.FragmentTodoListBinding
import de.ckitte.myapplication.main.ToDoApplication
import de.ckitte.myapplication.viewmodel.MainViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [ToDoListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ToDoListFragment : Fragment() {
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
        _binding = FragmentTodoListBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = MainViewModel(ToDoRepository(ToDoApplication().repository))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        setupClickListeners()
        fragmentTextUpdateObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setup the button in our fragment to call getUpdatedText method in viewModel
    private fun setupClickListeners() {
        binding.btnDoSomething.setOnClickListener { viewModel.getUpdatedText() }
    }

    // Observer is waiting for viewModel to update our UI
    private fun fragmentTextUpdateObserver() {
        viewModel.uiAlleToDos.observe(viewLifecycleOwner, Observer { updatedText ->
            binding.tvShowSomething.text = updatedText.toString()
        })
    }
}