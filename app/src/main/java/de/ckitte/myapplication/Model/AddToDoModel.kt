package de.ckitte.myapplication.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.repository.ToDoRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

class AddToDoModel(private val toDoDao: ToDoRepository) : ViewModel() {
    fun addToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoDao.addToDoItem(toDoItem)
    }

    fun getNewToDoItem(): ToDoItem {
        return ToDoItem(
            0,
            "",
            "",
            false,
            true,
            LocalDateTime.now(ZoneId.systemDefault()),
            ToDoRepository.defaultGroup
        )
    }
}