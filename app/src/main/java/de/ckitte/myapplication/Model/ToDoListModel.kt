package de.ckitte.myapplication.Model

import android.content.Context
import androidx.lifecycle.*
import de.ckitte.myapplication.Util.ListSort
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.repository.ToDoRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

// Es wird abgeraten, ein Context auf die View zu übergeben wegen möglicher
// Memory Leaks wegen unterschiedlicher Laufzeiten. Flow hingegen achtet den
// Status.
class ToDoListModel(toDoDao: ToDoRepository) : ViewModel() {
    val toDoRepository = toDoDao
    var toDos = toDoRepository.getAllToDosAsFlow_DateThenImportance().asLiveData()

    fun addToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoRepository.addToDoItem(toDoItem)
    }

    fun updateToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoRepository.updateToDoItem(toDoItem)
    }

    fun deleteToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoRepository.deleteToDoItem(toDoItem)
    }

    fun setCurrentToDoItem(toDoItem: ToDoItem) {
        ToDoRepository.setCurrentToDoItem(toDoItem)
    }

    fun refreshDatabase() {
        GlobalScope.launch {
            toDoRepository.RefreshDatabase()
        }
    }

    fun changeSortOrder(newSortOrder: ListSort) {
        when (newSortOrder) {
            ListSort.DateThenImportance -> this.toDos =
                toDoRepository.getAllToDosAsFlow_DateThenImportance().asLiveData()
            ListSort.ImportanceThenDate -> this.toDos =
                toDoRepository.getAllToDosAsFlow_ImportanceThenDate().asLiveData()
            else -> return
        }
    }
}