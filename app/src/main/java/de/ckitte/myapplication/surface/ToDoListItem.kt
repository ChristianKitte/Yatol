package de.ckitte.myapplication.surface

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import de.ckitte.myapplication.databinding.FragmentTodoListitemBinding

/**
 *
 * @property _binding FragmentTodoListitemBinding?
 * @property binding FragmentTodoListitemBinding
 */
class ToDoListItem : Fragment() {
    private var _binding: FragmentTodoListitemBinding? = null
    private val binding get() = _binding!!

    /**
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup?
     * @param savedInstanceState Bundle?
     * @return View
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        this._binding = FragmentTodoListitemBinding.inflate(inflater, container, false)

        val view = binding.root

        return view
    }
}