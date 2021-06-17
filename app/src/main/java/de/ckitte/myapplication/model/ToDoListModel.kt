package de.ckitte.myapplication.model

import androidx.lifecycle.*
import de.ckitte.myapplication.util.ListSort
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.repository.ToDoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Es wird abgeraten, ein Context auf die View zu übergeben wegen möglicher
// Memory Leaks wegen unterschiedlicher Laufzeiten. Flow hingegen achtet den
// Status.
class ToDoListModel(toDoDao: ToDoRepository) : ViewModel() {
    private val toDoRepository = toDoDao

    private val sortOrder = MutableStateFlow("DateThenImportance")

    @ExperimentalCoroutinesApi
    var toDos = sortOrder.flatMapLatest { currentCurrency ->
        // In case they return different types
        when (currentCurrency) {
            // Assuming all of these database calls return a Flow
            "DateThenImportance" -> toDoRepository.getAllToDosAsFlow_DateThenImportance()
            "ImportanceThenDate" -> toDoRepository.getAllToDosAsFlow_ImportanceThenDate()
            else -> toDoRepository.getAllToDosAsFlow_DateThenImportance()
        }
    }
        .asLiveData(Dispatchers.IO)


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
        CoroutineScope(Dispatchers.IO).launch {
            toDoRepository.refreshLocalDatabase()
        }
    }

    fun changeSortOrder(newSortOrder: ListSort) {
        when (newSortOrder) {
            ListSort.DateThenImportance -> {
                sortOrder.value = "DateThenImportance"
            }
            ListSort.ImportanceThenDate -> {
                sortOrder.value = "ImportanceThenDate"
            }
        }
    }
}