package de.ckitte.myapplication.model

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.util.getDisplayNameByUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class EditToDoModel(private val toDoDao: ToDoRepository) : ViewModel() {
    val toDoRepository = toDoDao

    private val currencyFlow = MutableStateFlow("default");

    var toDoContacts = currencyFlow.flatMapLatest { currentCurrency ->
        // In case they return different types
        when (currentCurrency) {
            // Assuming all of these database calls return a Flow
            "default" -> toDoRepository.getAllContacts()
            else -> toDoRepository.getAllContacts()
        }
    }
        .asLiveData(Dispatchers.IO);

    fun addToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoDao.addToDoItem(toDoItem)
    }

    fun updateToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        when (toDoItem.toDoId) {
            0 -> toDoDao.addToDoItem(toDoItem)
            else -> toDoDao.updateToDoItem(toDoItem)
        }
    }

    fun deleteToDoItem(toDoItem: ToDoItem) = viewModelScope.launch {
        toDoDao.deleteToDoItem(toDoItem)
    }

    fun getCurrentToDoItem(): ToDoItem? {
        return ToDoRepository.getCurrentToDoItem()
    }

    fun getNewToDoContact(): ToDoContact {
        return ToDoRepository.getNewContact()
    }

    fun addToDoContact(toDoContact: ToDoContact) = viewModelScope.launch {
        toDoDao.addToDoContacts(toDoContact)
    }

    fun deleteToDoContact(toDoContact: ToDoContact) = viewModelScope.launch {
        toDoDao.deleteToDoContacts(toDoContact)
    }

    fun getDisplayName(uri: Uri, contentResolver: ContentResolver?):String{
        return getDisplayNameByUri(uri, contentResolver!!)
    }
}