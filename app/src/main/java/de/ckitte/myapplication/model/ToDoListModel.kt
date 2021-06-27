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

/**
 * Model für die Verwendung im Kontext der Listenansicht von ToDos
 * @property toDoRepository ToDoRepository Eine Referenz auf das aktuelle Repository
 * @property sortOrder MutableStateFlow<String> Ein FlowObjekt für den Zugriff auf ToDos
 * @property toDos LiveData<List<LocalToDo>> Ein Observer eines FlowObjekt für den Zugriff auf ToDos
 * @constructor
 */
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

    //region CRUD ToDoItem

    /**
     * Löscht das übergeben ToDoItem und alle seine Kontakte
     * @param lokalToDo LocalToDo Das [LocalToDo] Element
     * @return Job Der iniziierte Task
     */
    fun deleteToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        toDoRepository.deleteToDoItem(lokalToDo)
    }

    /**
     * Aktualisiert das übergebene ToDoItem
     * @param lokalToDo LocalToDo Das [LocalToDo] Element
     * @return Job Der iniziierte Task
     */
    fun updateToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        toDoRepository.updateToDoItem(lokalToDo)
    }

    //endregion

    //region Handle neues ToDoItem

    /**
     * Setzt das übergebene ToDoItem als aktuelles Item
     * @param lokalToDo LocalToDo Das [LocalToDo] Element
     * @return Job Der iniziierte Task
     */
    fun setCurrentToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        ToDoRepository.setCurrentToDoItem(lokalToDo)
    }

    /**
     * Initialisiert ein neues ToDoItem und setzt es als aktuelles ToDoItem
     */
    fun iniNewToDoItem() {
        ToDoRepository.setCurrentToDoItem(ToDoRepository.getNewToDoItem())
    }

    //endregion

    //region Utils

    /**
     * Iniziiert einen Refresh der Datenbank
     */
    fun refreshDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            toDoRepository.refreshDatabase()
        }
    }

    /**
     * Iniziiert eine neue Sortierfolge der ToDos im Flow
     * @param newSortOrder ListSort Gibt die zu nutzende Sortierfolge als [ListSort] an
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

    //endregion
}