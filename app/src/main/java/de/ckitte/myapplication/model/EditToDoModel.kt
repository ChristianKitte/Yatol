package de.ckitte.myapplication.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.repository.ToDoRepository
import kotlinx.coroutines.launch

class EditToDoModel(private val toDoDao: ToDoRepository) : ViewModel() {
    fun addToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoDao.addToDoItem(toDoItem)
    }

    fun updateToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        when (toDoItem.toDoId) {
            0 -> toDoDao.addToDoItem(toDoItem)
            else -> toDoDao.updateToDoItem(toDoItem)
        }
    }

    fun deleteToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoDao.deleteToDoItem(toDoItem)
    }

    fun getCurrentToDoItem(): ToDoItem? {
        return ToDoRepository.getCurrentToDoItem()
    }
}