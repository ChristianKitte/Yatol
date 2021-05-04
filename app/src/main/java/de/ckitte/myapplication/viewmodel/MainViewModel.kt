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
    var uiAlleToDos: List<ToDo> = emptyList()
    fun test(): List<ToDo> {
        return uiAlleToDos
    }

    suspend fun getUpdatedText() {
        val updatedToDos = repository.getAllToDos()
        withContext(Dispatchers.IO) {
            uiAlleToDos = updatedToDos
        }
    }
}


