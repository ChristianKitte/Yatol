package de.ckitte.myapplication.model

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.util.getDisplayNameByUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class EditToDoModel(private val toDoDao: ToDoRepository) : ViewModel() {
    val toDoRepository = toDoDao

    private val currencyFlow = MutableStateFlow(0);

    var toDoContacts = currencyFlow.flatMapLatest { currentCurrency ->
        // In case they return different types
        when (currentCurrency) {
            // Assuming all of these database calls return a Flow
            0 -> toDoRepository.getAllContacts(ToDoRepository.getCurrentToDoItem()?.toDoId!!.toLong())
            else -> toDoRepository.getAllContacts(0)
        }
    }
        .asLiveData(Dispatchers.IO);

    fun addToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoDao.addToDoItem(toDoItem)
    }.invokeOnCompletion {
        commitContacts(toDoItem)
    }

    fun updateToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        when (toDoItem.toDoId) {
            0 -> toDoDao.addToDoItem(toDoItem)
            else -> toDoDao.updateToDoItem(toDoItem)
        }
    }.invokeOnCompletion {
        commitContacts(toDoItem)
    }

    fun deleteToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoDao.deleteToDoItem(toDoItem)
    }

    fun getCurrentToDoItem(): ToDoItem? {
        //currencyFlow.value=ToDoRepository.getCurrentToDoItem()?.toDoId!!
        return ToDoRepository.getCurrentToDoItem()
    }

    fun getNewToDoContact(): ToDoContact {
        return ToDoRepository.getNewContact()
    }

    fun addToDoContact(toDoContact: ToDoContact) = viewModelScope.launch {
        //toDoDao.markContactForAdd(toDoContact)
    }

    fun deleteToDoContact(toDoContact: ToDoContact) = viewModelScope.launch {
        //toDoDao.markContactForDelete(toDoContact)
    }

    fun commitContacts(toDoItem: ToDoItem) = viewModelScope.launch {
        //toDoDao.commitContacts()
    }

    fun rollbackContacts(toDoItem: ToDoItem) = viewModelScope.launch {
        //toDoDao.rollbackContacts()
    }

    fun getDisplayName(uri: Uri, contentResolver: ContentResolver?): String {
        return getDisplayNameByUri(uri, contentResolver!!)
    }
}