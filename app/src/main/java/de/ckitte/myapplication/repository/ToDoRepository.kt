package de.ckitte.myapplication.repository

import androidx.annotation.WorkerThread
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDoContact
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.entities.ToDoGroup
import de.ckitte.myapplication.firestore.FirestoreApi
import de.ckitte.myapplication.firestore.FirestoreBridgeUtil
import kotlinx.coroutines.flow.Flow
import de.ckitte.myapplication.util.ConnectionLiveData
import de.ckitte.myapplication.util.ContactState
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

class ToDoRepository(private val toDoDao: ToDoDao) {
    companion object StaticMembers {
        var defaultGroup: Long = 0

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
                toDoContactState = ContactState.transient.ordinal
            )
        }
    }

    // CRUD ToDoItem

    @WorkerThread
    suspend fun addToDoItem(vararg toDos: ToDoItem) {
        val api = FirestoreApi()

        toDos.forEach {
            val newID = toDoDao.addToDoItem(it)

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

            if (ConnectionLiveData.isConnected) {
                val firestoreToDoItem = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

                if (!it.toDoRemoteId.isBlank()) {
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

        toDos.forEach {
            toDoDao.deleteToDoItem(it)

            if (ConnectionLiveData.isConnected && !it.toDoRemoteId.isBlank()) {
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
                val firestoreToDoGroup = FirestoreBridgeUtil.getFirestoreItemFromDatabaseItem(it)

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

                if (!it.toDoGroupRemoteId.isBlank()) {
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

            if (ConnectionLiveData.isConnected && !it.toDoGroupRemoteId.isBlank()) {
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
        toDoDao.addToDoContacts(*toDoContacts)
    }

    @WorkerThread
    suspend fun updateToDoContacts(vararg toDoContacts: ToDoContact) {
        toDoDao.updateToDoContacts(*toDoContacts)
    }

    @WorkerThread
    suspend fun deleteToDoContacts(vararg toDoContacts: ToDoContact) {
        toDoDao.deleteToDoContacts(*toDoContacts)
    }

    // Flow und Observer

    // Für die Verwendung mit Flow und zur Nutzung mit einem Observer
    // ist dies Pattern notwendig. ACHTUNG: fun ohne suspend!
    //fun getAllToDosAsFlow_DateThenImportance(): Flow<List<ToDoItem>> =
    //    toDoDao.getAllToDosAsFlow_DateThenImportance()
    fun getAllToDosAsFlow_DateThenImportance(): Flow<List<ToDoItem>> =
        toDoDao.getAllToDosAsFlow_DateThenImportance()

    fun getAllToDosAsFlow_ImportanceThenDate(): Flow<List<ToDoItem>> =
        toDoDao.getAllToDosAsFlow_ImportanceThenDate()

    // Zusätzliche Funktionalität

    @WorkerThread
    suspend fun emptyLokalDatabase() {
        toDoDao.deleteAllToDoItems()
        toDoDao.deleteAllToDoGroups()
        toDoDao.deleteAllToDoContacts()

        ensureDefaultToDoGroup()
    }

    @WorkerThread
    suspend fun emptyRemoteDatabase() {
        val api = FirestoreApi()
        api.emptyStore()

        api.ensureDefaultGroup()
    }

    @WorkerThread
    suspend fun RefreshLocalDatabase() {
        emptyLokalDatabase()
        ensureDefaultToDoGroup()
        createSampleEntities()
    }

    @WorkerThread
    suspend fun ensureDefaultToDoGroup() {
        val todogroup = ToDoGroup(
            0,
            "",
            true,
            "Default",
            "Alle Einträge ohne Gruppe"
        )

        val id = toDoDao.addToDoGroup(todogroup)
        ToDoRepository.defaultGroup = id

        val api = FirestoreApi()
        api.ensureDefaultGroup()
    }

    @WorkerThread
    suspend fun createSampleEntities() {
        RepositoryHelper(toDoDao).createSampleEntities(defaultGroup)
    }
}