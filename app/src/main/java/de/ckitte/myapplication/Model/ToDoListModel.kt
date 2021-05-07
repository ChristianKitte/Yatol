package de.ckitte.myapplication.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.repository.ToDoRepository
import kotlinx.coroutines.launch

// Es wird abgeraten, ein Context auf die View zu übergeben wegen möglicher
// Memory Leaks wegen unterschiedlicher Laufzeiten. Flow hingegen achtet den
// Status.
class ToDoListModel(toDoDao: ToDoRepository) : ViewModel() {
    val toDos = toDoDao.getAllToDosAsFlow().asLiveData()

    fun insert(toDo: ToDoItem) = viewModelScope.launch {
        // code zum Einfügen....
    }
}