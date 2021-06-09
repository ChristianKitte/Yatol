package de.ckitte.myapplication.model

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.*
import de.ckitte.myapplication.util.ListSort
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.repository.ToDoRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

// Es wird abgeraten, ein Context auf die View zu übergeben wegen möglicher
// Memory Leaks wegen unterschiedlicher Laufzeiten. Flow hingegen achtet den
// Status.
class ToDoListModel(toDoDao: ToDoRepository) : ViewModel() {
    val toDoRepository = toDoDao
    var toDos = toDoRepository.getAllToDosAsFlow_DateThenImportance().asLiveData()
    //var toDos = toDoRepository.getAllToDosAsFlow_DateThenImportance().asLiveData()

    fun deleteToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoRepository.deleteToDoItem(toDoItem)
    }

    fun addToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoRepository.addToDoItem(toDoItem)
    }

    fun updateToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoRepository.updateToDoItem(toDoItem)
    }

    fun setCurrentToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        ToDoRepository.setCurrentToDoItem(toDoItem)
    }

    fun iniNewToDoItem() {
        ToDoRepository.setCurrentToDoItem(ToDoRepository.getNewToDoItem())
    }

    fun refreshDatabase() {
        GlobalScope.launch {
            toDoRepository.RefreshLocalDatabase()
        }
    }

    fun changeSortOrder(newSortOrder: ListSort) {
        when (newSortOrder) {
            ListSort.DateThenImportance -> {
                this.toDos =
                    toDoRepository.getAllToDosAsFlow_DateThenImportance().asLiveData()
            }
            ListSort.ImportanceThenDate -> {
                this.toDos =
                    toDoRepository.getAllToDosAsFlow_ImportanceThenDate().asLiveData()
                //toDos = toDosb
            }
            else -> return
        }
    }
}