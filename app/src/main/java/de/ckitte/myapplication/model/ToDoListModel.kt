package de.ckitte.myapplication.model

import androidx.lifecycle.*
import de.ckitte.myapplication.util.ListSort
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Es wird abgeraten, ein Context auf die View zu übergeben wegen möglicher
// Memory Leaks wegen unterschiedlicher Laufzeiten. Flow hingegen achtet den
// Status.
class ToDoListModel(toDoDao: ToDoRepository) : ViewModel() {
    val toDoRepository = toDoDao
    //var toDos = toDoRepository.getAllToDosAsFlow_DateThenImportance().asLiveData()
    //var toDos = toDoRepository.getAllToDosAsFlow_DateThenImportance().asLiveData()


    private val currencyFlow = MutableStateFlow("DateThenImportance");

    var toDos = currencyFlow.flatMapLatest { currentCurrency ->
        // In case they return different types
        when (currentCurrency) {
            // Assuming all of these database calls return a Flow
            "DateThenImportance" -> toDoRepository.getAllToDosAsFlow_DateThenImportance()
            "ImportanceThenDate" -> toDoRepository.getAllToDosAsFlow_ImportanceThenDate()
            else -> toDoRepository.getAllToDosAsFlow_DateThenImportance()
        }

        // OR in your case just call
        //serieDao.getStateFlow(currencyCode).map {
        //    with(stateMapper) { it.fromEntityToDomain() }
    }
        .asLiveData(Dispatchers.IO);


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
            toDoRepository.refreshLocalDatabase()
        }
    }

    fun changeSortOrder(newSortOrder: ListSort) {
        when (newSortOrder) {
            ListSort.DateThenImportance -> {
                currencyFlow.value = "DateThenImportance"
                //this.toDos =
                //    toDoRepository.getAllToDosAsFlow_DateThenImportance().asLiveData()
            }
            ListSort.ImportanceThenDate -> {
                currencyFlow.value = "ImportanceThenDate"
                //this.toDos =
                //    toDoRepository.getAllToDosAsFlow_ImportanceThenDate().asLiveData()
                //toDos = toDosb
            }
            else -> return
        }
    }
}