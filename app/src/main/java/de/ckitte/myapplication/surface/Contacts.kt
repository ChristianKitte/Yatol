package de.ckitte.myapplication.surface

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import de.ckitte.myapplication.Model.EditToDoModel
import de.ckitte.myapplication.R
import de.ckitte.myapplication.databinding.FragmentContactsBinding
import de.ckitte.myapplication.databinding.FragmentEditTodoBinding

class ContactsFragment : Fragment(R.layout.fragment_contacts) {
    //private lateinit var _viewModel: EditToDoModel
    private lateinit var _binding: FragmentContactsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentContactsBinding.bind(view)

        _binding.apply {
            btnBack.setOnClickListener {
                it.findNavController().navigate(R.id.action_contactsFragment_to_editToDo)
            }

            btnSave.setOnClickListener {
                it.findNavController().navigate(R.id.action_contactsFragment_to_editToDo)
            }

        }
    }

}