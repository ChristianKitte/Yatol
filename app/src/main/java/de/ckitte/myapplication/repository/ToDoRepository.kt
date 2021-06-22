package de.ckitte.myapplication.repository

import android.util.Log
import androidx.annotation.WorkerThread
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.database.entities.ToDoGroup
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.firestore.FirestoreApi
import de.ckitte.myapplication.firestore.FirestoreBridgeUtil
import de.ckitte.myapplication.util.ConnectionLiveData
import de.ckitte.myapplication.util.ContactState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class ToDoRepository(private val toDoDao: ToDoDao) {
    companion object StaticMembers {
        val defaultGroup: Long = 0

        @Volatile
        private var currentToDoItem: ToDoItem? = null

        fun setCurrentToDoItem(currentToDoItem: ToDoItem) {
            this.currentToDoItem = currentToDoItem
        }

        fun getCurrentToDoItem(): ToDoItem? {
            return this.currentToDoItem

        }

        fun getNewToDoItem(): ToDoItem {
            return ToDoItem(
                0,
                "",
                "",
                "",
                false,
                false,
                LocalDateTime.now(),
                ToDoRepository.defaultGroup
            )
        }

        fun getNewContact(): ToDoContact {
            return ToDoContact(
                toDoContactId = 0,
                toDoContactRemoteId = "",
                toDoContactHostId = "",
                toDoItemId = 0,
                toDoItemRemoteId = "",
                toDoContactState = ContactState.Added.ordinal
            )
        }
    }

    // CRUD ToDoItem

    @WorkerThread
    suspend fun addToDoItem(vararg toDos: ToDoItem) {
        val api = FirestoreApi()

        toDos.forEach {
            val newID = toDoDao.addToDoItem(it)
            it.toDoId = newID.toInt()
            setCurrentToDoItem(it)

            if (ConnectionLiveData.isConnected) {
                val firestoreToDoItem = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                val insertedFirestoreToDoItem = api.insertToDoItem(
                    FirestoreApi.getToDoItemCollection,
                    firestoreToDoItem
                )

                toDoDao.updateRemoteToDoItemId(insertedFirestoreToDoItem.toDoId, newID)
            }
        }
    }

    @WorkerThread
    suspend fun updateToDoItem(vararg toDos: ToDoItem) {
        val api = FirestoreApi()

        toDos.forEach {
            toDoDao.updateToDoItem(it)
            setCurrentToDoItem(it)

            if (ConnectionLiveData.isConnected) {
                val firestoreToDoItem = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                if (it.toDoRemoteId.isNotBlank()) {
                    api.updateToDoItem(
                        FirestoreApi.getToDoItemCollection,
                        firestoreToDoItem
                    )
                } else if (it.toDoRemoteId.isBlank()) {
                    val insertedFirestoreToDoItem = api.insertToDoItem(
                        FirestoreApi.getToDoItemCollection,
                        firestoreToDoItem
                    )

                    toDoDao.updateRemoteToDoItemId(
                        insertedFirestoreToDoItem.toDoId,
                        it.toDoId.toLong()
                    )
                }
            }
        }
    }

    @WorkerThread
    suspend fun deleteToDoItem(vararg toDos: ToDoItem) {
        val api = FirestoreApi()
        setCurrentToDoItem(getNewToDoItem())

        toDos.forEach {
            toDoDao.deleteToDoItem(it)

            if (ConnectionLiveData.isConnected && it.toDoRemoteId.isNotBlank()) {
                val firestoreToDoItem = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                api.deleteToDoItem(
                    FirestoreApi.getToDoItemCollection,
                    firestoreToDoItem
                )
            }
        }
    }

    // CRUD ToDoGroupItem

    @WorkerThread
    suspend fun addToDoGroup(vararg toDoGroups: ToDoGroup) {
        val api = FirestoreApi()

        toDoGroups.forEach {
            val newID = toDoDao.addToDoGroup(it)

            if (ConnectionLiveData.isConnected) {

                val firestoreToDoGroup =
                    FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                val insertedFirestoreToDoGroup = api.insertToDoGroup(
                    FirestoreApi.getToDoGroupCollection,
                    firestoreToDoGroup
                )

                toDoDao.updateRemoteToDoGroupId(insertedFirestoreToDoGroup.toDoGroupId, newID)
            }
        }
    }

    @WorkerThread
    suspend fun updateToDoGroup(vararg toDoGroups: ToDoGroup) {
        val api = FirestoreApi()

        toDoGroups.forEach {
            toDoDao.updateToDoGroup(it)

            if (ConnectionLiveData.isConnected) {
                val firestoreToDoGroup = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                if (it.toDoGroupRemoteId.isNotBlank()) {
                    api.updateToDoGroup(
                        FirestoreApi.getToDoGroupCollection,
                        firestoreToDoGroup
                    )
                } else if (it.toDoGroupRemoteId.isBlank()) {
                    val insertedFirestoreToDoGroup = api.insertToDoGroup(
                        FirestoreApi.getToDoGroupCollection,
                        firestoreToDoGroup
                    )

                    toDoDao.updateRemoteToDoGroupId(
                        insertedFirestoreToDoGroup.toDoGroupId,
                        it.toDoGroupId.toLong()
                    )
                }
            }
        }
    }

    @WorkerThread
    suspend fun deleteToDoGroup(vararg toDoGroups: ToDoGroup) {
        val api = FirestoreApi()

        toDoGroups.forEach {
            toDoDao.deleteToDoGroup(it)

            if (ConnectionLiveData.isConnected && it.toDoGroupRemoteId.isNotBlank()) {
                val firestoreToDoGroup = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                api.deleteToDoGroup(
                    FirestoreApi.getToDoItemCollection,
                    firestoreToDoGroup
                )
            }
        }
    }

    // CRUD ToDoContacts

    @WorkerThread
    suspend fun addToDoContacts(vararg toDoContacts: ToDoContact) {
        val api = FirestoreApi()

        toDoContacts.forEach {
            val newID = toDoDao.addToDoContact(it)
            it.toDoContactId = newID.toInt()

            if (ConnectionLiveData.isConnected) {
                val firestoreToDoContact =
                    FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                val insertedFirestoreToDoContact = api.insertToDoContact(
                    FirestoreApi.getToDoContactCollection,
                    firestoreToDoContact
                )

                toDoDao.updateRemoteToDoContactId(
                    insertedFirestoreToDoContact.toDoContactID,
                    it.toDoContactId.toLong()
                )
            }
        }
    }

    @WorkerThread
    suspend fun updateToDoContact(vararg toDoContacts: ToDoContact) {
        val api = FirestoreApi()

        try {
            toDoContacts.forEach {
                toDoDao.updateToDoContact(it)

                if (ConnectionLiveData.isConnected) {
                    val firestoreToDoContact =
                        FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                    if (it.toDoContactRemoteId.isNotBlank()) {
                        api.updateToDoContact(
                            FirestoreApi.getToDoContactCollection,
                            firestoreToDoContact
                        )
                    } else if (it.toDoContactRemoteId.isBlank()) {
                        val insertedFirestoreToDoContact = api.insertToDoContact(
                            FirestoreApi.getToDoContactCollection,
                            firestoreToDoContact
                        )

                        toDoDao.updateRemoteToDoContactId(
                            insertedFirestoreToDoContact.toDoContactID,
                            it.toDoContactId.toLong()
                        )
                    }
                }
            }
        } catch (d: Exception) {
            Log.println(Log.DEBUG, "updateToDoContact", d.toString())
        }
    }

    @WorkerThread
    suspend fun deleteToDoContacts(vararg toDoContacts: ToDoContact) {
        val api = FirestoreApi()

        toDoContacts.forEach {
            toDoDao.deleteToDoContact(it)

            if (ConnectionLiveData.isConnected && it.toDoContactRemoteId.isNotBlank()) {
                val firestoreToDoContact = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                api.deleteToDoContact(
                    FirestoreApi.getToDoContactCollection,
                    firestoreToDoContact
                )
            }
        }
    }

    @WorkerThread
    suspend fun commitContacts() {
        val contactsToDelete = toDoDao.getAllDeletedToDoContacts()
        val contactsToAdd = toDoDao.getAllAddedToDoContacts()

        contactsToDelete.forEach {
            deleteToDoContacts(it)
        }

        contactsToAdd.forEach {
            it.toDoContactState = ContactState.Save.ordinal
            updateToDoContact(it)
        }
    }

    @WorkerThread
    suspend fun rollbackContacts() {
        val contactsTouched = toDoDao.getAllTouchedToDoContacts()

        contactsTouched.forEach {
            when (it.toDoContactState) {
                ContactState.Added.ordinal -> {
                    deleteToDoContacts(it)
                }
                ContactState.Deleted.ordinal -> {
                    it.toDoContactState = ContactState.Save.ordinal
                    updateToDoContact(it)
                }
            }
        }
    }

    // Flow und Observer

    // Für die Verwendung mit Flow und zur Nutzung mit einem Observer
    // zwei Pattern. Hier: fun ohne suspend!
    fun getAllToDosAsFlow_DateThenImportance(): Flow<List<ToDoItem>> =
        toDoDao.getAllToDosAsFlowByDateThenImportance()

    fun getAllToDosAsFlow_ImportanceThenDate(): Flow<List<ToDoItem>> =
        toDoDao.getAllToDosAsFlowByImportanceThenDate()

    fun getAllContacts(toDoItemID: Long): Flow<List<ToDoContact>> =
        toDoDao.getAllContacts(toDoItemID)

    // Zusätzliche Funktionalität

    @WorkerThread
    suspend fun emptyLokalDatabase() {
        toDoDao.deleteAllToDoItems()
        toDoDao.deleteAllToDoGroups()
        toDoDao.deleteAllToDoContacts()
    }

    @WorkerThread
    suspend fun emptyRemoteDatabase() {
        val api = FirestoreApi()
        api.emptyStore()
    }

    @WorkerThread
    suspend fun refreshLocalDatabase() {
        //lokale ToDos ==> Remote löschen und lokal nach Remote
        //Keine lokale ToDos ==> Alle Daten aus Remote holen

        val lokalDs = getLokalToDosCount()

        if (lokalDs > 0) {
            //mirrorToRemote()
        } else {
            // mirrorFromSample()
            //mirrorFromRemote()
        }
    }

    private suspend fun getLokalToDosCount(): Long {
        return toDoDao.getLokalToDosCount()
    }

    suspend fun mirrorFromSample() {
        createSampleEntities()
    }

    //mehr Zeit?
    suspend fun mirrorToRemote() {
        if (ConnectionLiveData.isConnected) {
            val api = FirestoreApi()

            emptyRemoteDatabase()

            // Die Unterstützung für Gruppen ist in dieser Version nur grundlegend vorgesehen,
            // aber nicht abschließend implementiert. Daher können hier alle ggf. vorhandenen
            // Gruppen einfach übernommen werden.
            mirrorToDoGroupsToRemote()

            val toDoItems = toDoDao.getAllLokalToDos()
            toDoItems.forEach {
                val firestoreToDoItem = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                val insertedFirestoreToDoItem = api.insertToDoItem(
                    FirestoreApi.getToDoItemCollection,
                    firestoreToDoItem
                )

                toDoDao.updateRemoteToDoItemId(insertedFirestoreToDoItem.toDoId, it.toDoId.toLong())

                // Das Item wurde neu eingefügt und hat eine neue ID erhalten. Daher müssen bei
                // der Spiegelung der Kontakte diese ebenfalls angepasst werden!
                //mirrorToDoContactsToRemoteByToDo(
                //    it.toDoId.toLong(),
                //    insertedFirestoreToDoItem.toDoId
                //)
            }
        }
    }

    suspend fun mirrorToDoGroupsToRemote() {
        if (ConnectionLiveData.isConnected) {
            val api = FirestoreApi()

            val toDoGroups = toDoDao.getAllToDoGroups()
            toDoGroups.forEach {
                val firestoreToDoGroup = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                val insertedFirestoreToDoGroup = api.insertToDoGroup(
                    FirestoreApi.getToDoItemCollection,
                    firestoreToDoGroup
                )

                toDoDao.updateRemoteToDoGroupId(
                    insertedFirestoreToDoGroup.toDoGroupId,
                    it.toDoGroupId.toLong()
                )
            }
        }
    }

    private suspend fun mirrorToDoContactsToRemoteByToDo(
        toDoItemId: Long,
        toDoItemRemoteId: String
    ) {
        if (ConnectionLiveData.isConnected) {
            val api = FirestoreApi()

            val toDoItemContacts = toDoDao.getAllToDoContacts(toDoItemId)
            toDoItemContacts.forEach {
                val firestoreToDoContact = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)
                firestoreToDoContact.toDoId = toDoItemRemoteId

                val insertedFirestoreToDoContact = api.insertToDoContact(
                    FirestoreApi.getToDoContactCollection,
                    firestoreToDoContact
                )

                toDoDao.updateRemoteToDoContactId(
                    firestoreToDoContact.toDoContactID,
                    it.toDoContactId.toLong()
                )
            }
        }
    }

    fun mirrorFromRemote() {
        // lokal shall be empty
        // load all from Remote
    }

    @WorkerThread
    suspend fun createSampleEntities() {
        RepositoryHelper(toDoDao).createSampleEntities(defaultGroup)
    }
}