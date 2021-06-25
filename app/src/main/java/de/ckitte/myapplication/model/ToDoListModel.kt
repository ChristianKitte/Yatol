package de.ckitte.myapplication.model

import androidx.lifecycle.*
import de.ckitte.myapplication.util.ListSort
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.repository.ToDoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Es wird abgeraten, ein Context auf die View zu übergeben wegen möglicher
// Memory Leaks wegen unterschiedlicher Laufzeiten. Flow hingegen achtet den
// Status.

/**
 *
 * @property toDoRepository ToDoRepository
 * @property sortOrder MutableStateFlow<String>
 * @property toDos LiveData<List<LocalToDo>>
 * @constructor
 */
class ToDoListModel(toDoDao: ToDoRepository) : ViewModel() {
    /**
     *
     */
    private val toDoRepository = toDoDao

    /**
     *
     */
    private val sortOrder = MutableStateFlow("DateThenImportance")

    /**
     *
     */
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

    /**
     *
     * @param lokalToDo LocalToDo
     * @return Job
     */
    fun deleteToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        toDoRepository.deleteToDoItem(lokalToDo)
    }

    /**
     *
     * @param lokalToDo LocalToDo
     * @return Job
     */
    fun addToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        toDoRepository.addToDoItem(lokalToDo)
    }

    /**
     *
     * @param lokalToDo LocalToDo
     * @return Job
     */
    fun updateToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        toDoRepository.updateToDoItem(lokalToDo)
    }

    /**
     *
     * @param lokalToDo LocalToDo
     * @return Job
     */
    fun setCurrentToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        ToDoRepository.setCurrentToDoItem(lokalToDo)
    }

    /**
     *
     */
    fun iniNewToDoItem() {
        ToDoRepository.setCurrentToDoItem(ToDoRepository.getNewToDoItem())
    }

    /**
     *
     */
    fun refreshDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            toDoRepository.refreshDatabase()
        }
    }

    /**
     *
     * @param newSortOrder ListSort
     */
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