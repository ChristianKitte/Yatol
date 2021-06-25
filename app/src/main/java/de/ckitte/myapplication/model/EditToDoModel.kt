package de.ckitte.myapplication.model

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import de.ckitte.myapplication.database.entities.LocalToDoContact
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**
 *
 * @property toDoDao ToDoRepository
 * @property toDoRepository ToDoRepository
 * @property contactFilter MutableStateFlow<Int>
 * @property toDoContacts LiveData<List<LocalToDoContact>>
 * @constructor
 */
class EditToDoModel(private val toDoDao: ToDoRepository) : ViewModel() {
    /**
     *
     */
    private val toDoRepository = toDoDao

    /**
     *
     */
    private val contactFilter = MutableStateFlow(0);

    /**
     *
     */
    @ExperimentalCoroutinesApi
    var toDoContacts = contactFilter.flatMapLatest { currentCurrency ->
        // In case they return different types
        when (currentCurrency) {
            // Assuming all of these database calls return a Flow
            0 -> toDoRepository.getAllContacts(ToDoRepository.getCurrentToDoItem()?.toDoLocalId!!.toLong())
            else -> toDoRepository.getAllContacts(0)
        }
    }
        .asLiveData(Dispatchers.IO);

    /**
     *
     * @param lokalToDo LocalToDo
     * @return DisposableHandle
     */
    fun addToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        toDoDao.addToDoItem(lokalToDo)
    }.invokeOnCompletion {
        commitToDoContacts()
    }


    /**
     *
     * @param lokalToDo LocalToDo
     * @return DisposableHandle
     */
    fun updateToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        when (lokalToDo.toDoLocalId) {
            0 -> toDoDao.addToDoItem(lokalToDo)
            else -> toDoDao.updateToDoItem(lokalToDo)
        }
    }.invokeOnCompletion {
        commitToDoContacts()
    }

    /**
     *
     * @param lokalToDo LocalToDo
     * @return Job
     */
    fun deleteToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        toDoDao.deleteToDoItem(lokalToDo)
    }

    fun getCurrentToDoItem(): LocalToDo? {
        return ToDoRepository.getCurrentToDoItem()
    }

    /**
     *
     * @return LocalToDoContact
     */
    fun getNewToDoContact(): LocalToDoContact {
        return ToDoRepository.getNewToDoContact()
    }

    /**
     *
     * @param toDoContact LocalToDoContact
     * @return Job
     */
    fun addToDoContact(toDoContact: LocalToDoContact) = viewModelScope.launch {
        toDoContact.toDoContactLocalState = ToDoContactState.Added.ordinal
        toDoDao.addToDoContacts(toDoContact)
    }

    /**
     *
     * @param toDoContact LocalToDoContact
     * @return Job
     */
    fun deleteToDoContact(toDoContact: LocalToDoContact) = viewModelScope.launch {
        toDoContact.toDoContactLocalState = ToDoContactState.Deleted.ordinal
        toDoDao.updateToDoContact(toDoContact)
    }

    /**
     *
     * @param lokalToDo LocalToDo
     * @return Job
     */
    fun commitToDoContacts() = viewModelScope.launch {
        toDoDao.commitTransientToDoContacts()
    }

    /**
     *
     * @param lokalToDo LocalToDo
     * @return Job
     */
    fun rollbackToDoContacts() = viewModelScope.launch {
        toDoDao.rollbackTransientToDoContacts()
    }

    /**
     *
     * @param uri Uri
     * @param contentResolver ContentResolver?
     * @return String
     */
    fun getDisplayName(uri: Uri, contentResolver: ContentResolver?): String {
        return getDisplayNameByUri(uri, contentResolver!!)
    }

    /**
     *
     * @param uri Uri
     * @param contentResolver ContentResolver?
     * @return String
     */
    fun getPhoneNumber(uri: Uri, contentResolver: ContentResolver?): String {
        return getPhoneNumberByUri(uri, contentResolver!!)
    }

    /**
     *
     * @param uri Uri
     * @param contentResolver ContentResolver?
     * @return String
     */
    fun getEmailAdress(uri: Uri, contentResolver: ContentResolver?): String {
        return getEmailAdressByUri(uri, contentResolver!!)
    }
}