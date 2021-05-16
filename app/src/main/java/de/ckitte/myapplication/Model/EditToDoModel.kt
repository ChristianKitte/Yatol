package de.ckitte.myapplication.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.repository.ToDoRepository
import kotlinx.coroutines.launch

class EditToDoModel(private val toDoDao: ToDoRepository) : ViewModel() {
    fun updateToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoDao.updateToDoItem(toDoItem)
    }

    fun deleteToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoDao.deleteToDoItem(toDoItem)
    }

    fun getCurrentToDoItem(): ToDoItem? {
        return ToDoRepository.getCurrentToDoItem()
    }
}