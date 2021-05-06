package de.ckitte.myapplication.viewmodel

import androidx.lifecycle.*
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.repository.ToDoRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList

class MainViewModel(private val toDoDao: ToDoDao?) : ViewModel() {
    //var uiAlleToDos: LiveData<List<ToDo>> = repository.allToDos.asLiveData()

    var toDos = toDoDao?.getAllToDosFlow()?.asLiveData()
    var s = 0

    fun insert(toDo: ToDo) = viewModelScope.launch {
        //repository.addToDo(toDo)
    }

    /*
    fun test() {

    }

    var x: String = ""
    @InternalCoroutinesApi
    fun getUpdatedText() {
        //Holen der Daten aus Query und einschießen
        GlobalScope.launch {
            val updatedToDos = repository.allToDosFlow.collect({
                value->println()
            })

            updatedToDos.collect {
                    value-> println(value)
            }
        }

*/

    //withContext(Dispatchers.IO) {
    //    x += updatedToDos[0].toDoTitle
    //}
    //}
}
/*
class WordViewModelFactory(private val repository: ToDoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
*/