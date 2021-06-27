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
 * Model für die Verwendung im Kontext Anlegen und Editieren von ToDos
 * @property toDoDao ToDoRepository Eine Referenz auf das aktuelle Repository
 * @property toDoRepository ToDoRepository  Eine Referenz auf das aktuelle Repository
 * @property contactFilter MutableStateFlow<Int> Ein FlowObjekt für den Zugriff auf Kontakte
 * @property toDoContacts LiveData<List<LocalToDoContact>> Ein Observer eines FlowObjekt für den Zugriff auf Kontakte
 * @constructor
 */
class EditToDoModel(private val toDoDao: ToDoRepository) : ViewModel() {
    private val toDoRepository = toDoDao
    private val contactFilter = MutableStateFlow(0)

    @ExperimentalCoroutinesApi
    var toDoContacts = contactFilter.flatMapLatest { currentContact ->
        // In case they return different types
        when (currentContact) {
            // Assuming all of these database calls return a Flow
            0 -> toDoRepository.getAllLocalValidToDoContactsByToDo(ToDoRepository.getCurrentToDoItem()?.toDoLocalId!!.toLong())
            else -> toDoRepository.getAllLocalValidToDoContactsByToDo(0)
        }
    }
        .asLiveData(Dispatchers.IO);

    //region ToDoItem

    /**
     * Aktualisiert das aktuelle ToDoItem und führt einen Commit für seine Kontakte aus
     * @param lokalToDo LocalToDo Das aktuelle [LocalToDo] Element
     * @return DisposableHandle Ein Handle auf Objekt, das für die Garbage Collection freigegeben werden kann.
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
     * Löscht das aktuelle ToDoItem und alle seine Kontakte. Im Anschluss erfolgt eine Weiterleitung auf die Hauptseite
     * @param lokalToDo LocalToDo Das aktuelle [LocalToDo] Element
     * @return Job Der iniziierte Task
     */
    fun deleteToDoItem(lokalToDo: LocalToDo) = viewModelScope.launch {
        toDoDao.deleteToDoItem(lokalToDo)
    }

    /**
     * Gibt das atuelle ToDoItem zurück
     * @return LocalToDo? Das aktuelle [LocalToDo] Element
     */
    fun getCurrentToDoItem(): LocalToDo? {
        return ToDoRepository.getCurrentToDoItem()
    }

    //endregion

    //region ToDoContact

    /**
     * Gibt ein neues ToDoContact Element zurück
     * @return LocalToDoContact Das aktuelle [LocalToDoContact] Element
     */
    fun getNewToDoContact(): LocalToDoContact {
        return ToDoRepository.getNewToDoContact()
    }

    /**
     * Fügt das übergebene ToDoContact Element den Kontakten des aktuelle ToDos hinzu
     * @param toDoContact LocalToDoContact Das [LocalToDoContact] Element
     * @return Job Der iniziierte Task
     */
    fun addToDoContact(toDoContact: LocalToDoContact) = viewModelScope.launch {
        toDoContact.toDoContactLocalState = ToDoContactState.Added.ordinal
        toDoDao.addToDoContacts(toDoContact)
        contactFilter.value = 0
    }

    /**
     * Löscht das übergebene ToDoContact Element aus den Kontakten des aktuelle ToDos
     * @param toDoContact LocalToDoContact Das [LocalToDoContact] Element
     * @return Job Der iniziierte Task
     */
    fun deleteToDoContact(toDoContact: LocalToDoContact) = viewModelScope.launch {
        toDoContact.toDoContactLocalState = ToDoContactState.Deleted.ordinal
        toDoDao.updateToDoContact(toDoContact)
    }

    //endregion

    //region Commit und Rollback der Kontakte

    /**
     * Führt einen Commit für alle ToDoContacts des aktuellen ToDos aus
     * @return Job Der iniziierte Task
     */
    fun commitToDoContacts() = viewModelScope.launch {
        toDoDao.commitTransientToDoContacts()
    }

    /**
     * Führt einen Rollback für alle ToDoContacts des aktuellen ToDos aus
     * @param lokalToDo LocalToDo
     * @return Job Der iniziierte Task
     */
    fun rollbackToDoContacts() = viewModelScope.launch {
        toDoDao.rollbackTransientToDoContacts()
    }

    //endregion

    //region Informationen zu Kontakten

    /**
     * Gibt den Namen des zu der URI gehörenden Kontaktes zurück
     * @param uri Uri Die URI eines Kontaktes
     * @param contentResolver ContentResolver? Ein gültiges ContentResolver Objekt
     * @return String Der Name des Kontaktes
     */
    fun getDisplayName(uri: Uri, contentResolver: ContentResolver?): String {
        return getDisplayNameByUri(uri, contentResolver!!)
    }

    /**
     * Gibt die Telefonnummer des zu der URI gehörenden Kontaktes zurück
     * @param uri Uri Die URI eines Kontaktes
     * @param contentResolver ContentResolver? Ein gültiges ContentResolver Objekt
     * @return String Die Telfonnummer des Kontaktes
     */
    fun getPhoneNumber(uri: Uri, contentResolver: ContentResolver?): String {
        return getPhoneNumberByUri(uri, contentResolver!!)
    }

    /**
     * Gibt die eMail des zu der URI gehörenden Kontaktes zurück
     * @param uri Uri Die URI eines Kontaktes
     * @param contentResolver ContentResolver? Ein gültiges ContentResolver Objekt
     * @return String Die eMail des Kontaktes
     */
    fun getEmailAdress(uri: Uri, contentResolver: ContentResolver?): String {
        return getEmailAdressByUri(uri, contentResolver!!)
    }

    //endregion
}