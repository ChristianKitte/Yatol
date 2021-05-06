package de.ckitte.myapplication.viewmodel

import androidx.lifecycle.*
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.repository.ToDoRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val repository: ToDoRepository) : ViewModel() {
    var uiAlleToDos: LiveData<List<ToDo>> = repository.allToDos.asLiveData()

    fun insert(toDo: ToDo) = viewModelScope.launch {
        repository.addToDo(toDo)
    }

    fun test() {

    }

    suspend fun getUpdatedText() {
        //Holen der Daten aus Query und einschießen
        //val updatedToDos = repository.getAllToDos()
        //withContext(Dispatchers.IO) {
        //uiAlleToDos = updatedToDos
        //}
    }
}

class WordViewModelFactory(private val repository: ToDoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
