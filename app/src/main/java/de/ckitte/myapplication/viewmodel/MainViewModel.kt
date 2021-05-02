package de.ckitte.myapplication.viewmodel

import androidx.lifecycle.*
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.repository.ToDoRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList

class MainViewModel(private val repository: ToDoRepository) : ViewModel() {
    //private val toDosDb: LiveData<List<ToDo>> = repository.getAllToDos.asLiveData()
    //fun allToDos(): LiveData<List<ToDo>> = toDosDb

    // Hier sind Daten, welche verwendet werden können. Änderungen führen zu
    // einer Information der Oberfläche
    val uiAlleToDos = MutableLiveData<Flow<List<ToDo>>>()

    // Ruft die Änderungen ab
    fun getUpdatedText() {
        val updatedToDos = repository.getAllToDos()
        uiAlleToDos.postValue(updatedToDos)
    }

}
