package de.ckitte.myapplication.model

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.ckitte.myapplication.database.entities.LokalToDoContact
import de.ckitte.myapplication.database.entities.LokalToDo
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class EditToDoModel(private val toDoDao: ToDoRepository) : ViewModel() {
    private val toDoRepository = toDoDao

    private val contactFilter = MutableStateFlow(0);

    var toDoContacts = contactFilter.flatMapLatest { currentCurrency ->
        // In case they return different types
        when (currentCurrency) {
            // Assuming all of these database calls return a Flow
            0 -> toDoRepository.getAllContacts(ToDoRepository.getCurrentToDoItem()?.toDoLocalId!!.toLong())
            else -> toDoRepository.getAllContacts(0)
        }
    }
        .asLiveData(Dispatchers.IO);

    fun addToDoItem(lokalToDo: LokalToDo) = viewModelScope.launch {
        toDoDao.addToDoItem(lokalToDo)
    }.invokeOnCompletion {
        commitToDoContacts(lokalToDo)
    }

    fun updateToDoItem(lokalToDo: LokalToDo) = viewModelScope.launch {
        when (lokalToDo.toDoLocalId) {
            0 -> toDoDao.addToDoItem(lokalToDo)
            else -> toDoDao.updateToDoItem(lokalToDo)
        }
    }.invokeOnCompletion {
        commitToDoContacts(lokalToDo)
    }

    fun deleteToDoItem(lokalToDo: LokalToDo) = viewModelScope.launch {
        toDoDao.deleteToDoItem(lokalToDo)
    }

    fun getCurrentToDoItem(): LokalToDo? {
        return ToDoRepository.getCurrentToDoItem()
    }

    fun getNewToDoContact(): LokalToDoContact {
        return ToDoRepository.getNewToDoContact()
    }

    fun addToDoContact(toDoContact: LokalToDoContact) = viewModelScope.launch {
        toDoContact.toDoContactLocalState = ToDoContactState.Added.ordinal
        toDoDao.addToDoContacts(toDoContact)
    }

    fun deleteToDoContact(toDoContact: LokalToDoContact) = viewModelScope.launch {
        toDoContact.toDoContactLocalState = ToDoContactState.Deleted.ordinal
        toDoDao.updateToDoContact(toDoContact)
    }

    fun commitToDoContacts(lokalToDo: LokalToDo) = viewModelScope.launch {
        toDoDao.commitTransientToDoContacts()
    }

    fun rollbackToDoContacts(lokalToDo: LokalToDo) = viewModelScope.launch {
        toDoDao.rollbackTransientToDoContacts()
    }

    fun getDisplayName(uri: Uri, contentResolver: ContentResolver?): String {
        return getDisplayNameByUri(uri, contentResolver!!)
    }

    fun getPhoneNumber(uri: Uri, contentResolver: ContentResolver?): String {
        return getPhoneNumberByUri(uri, contentResolver!!)
    }

    fun getEmailAdress(uri: Uri, contentResolver: ContentResolver?): String {
        return getEmailAdressByUri(uri, contentResolver!!)
    }
}