package de.ckitte.myapplication.repository

import androidx.annotation.WorkerThread
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDoContacts
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.entities.ToDoGroup
import de.ckitte.myapplication.firestore.FirestoreApi
import de.ckitte.myapplication.firestore.firestoreEntities.firestoreToDoItem
import kotlinx.coroutines.flow.Flow
import de.ckitte.myapplication.util.ConnectionLiveData
import java.time.LocalDateTime

class ToDoRepository(private val toDoDao: ToDoDao) {
    // Statische Eigenschaften

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
    }

    // CRUD ToDoItem

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addToDoItem(vararg toDos: ToDoItem) {
        toDoDao.addToDoItem(*toDos)

        val api = FirestoreApi()
        if (ConnectionLiveData.isConnected) {
            for (toDoItem in toDos) {
                val firestoreToDoItem = firestoreToDoItem(
                    toDoId = "",
                    toDoTitle = toDoItem.toDoTitle,
                    toDoDescription = toDoItem.toDoDescription,
                    toDoIsDone = toDoItem.toDoIsDone,
                    toDoIsFavourite = toDoItem.toDoIsFavourite,
                    toDoDoUntil = toDoItem.toDoDoUntil,
                    toDoGroupId = FirestoreApi.defaultGroupID,
                    user = ""
                )

                api.insertToDoItem(FirestoreApi.getToDoItemCollection, firestoreToDoItem)
            }
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateToDoItem(vararg toDos: ToDoItem) {
        toDoDao.updateToDoItem(*toDos)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteToDoItem(vararg toDos: ToDoItem) {
        toDoDao.deleteToDoItem(*toDos)

        val api = FirestoreApi()
        if (ConnectionLiveData.isConnected) {
            for (toDoItem in toDos) {
                val firestoreToDoItem = firestoreToDoItem(
                    toDoId = "",
                    toDoTitle = toDoItem.toDoTitle,
                    toDoDescription = toDoItem.toDoDescription,
                    toDoIsDone = toDoItem.toDoIsDone,
                    toDoIsFavourite = toDoItem.toDoIsFavourite,
                    toDoDoUntil = toDoItem.toDoDoUntil,
                    toDoGroupId = FirestoreApi.defaultGroupID,
                    user = ""
                )

                api.deleteToDoItem(FirestoreApi.getToDoItemCollection, firestoreToDoItem)
            }
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllToDoItems() {
        toDoDao.deleteAllToDoItems()
    }

    // CRUD ToDoGroupItem

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addToDoGroup(toDoGroup: ToDoGroup): Long {
        return toDoDao.addToDoGroup(toDoGroup)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addToDoGroup(vararg toDoGroup: ToDoGroup) {
        toDoDao.addToDoGroup(*toDoGroup)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateToDoGroup(vararg toDoGroups: ToDoGroup) {
        toDoDao.updateToDoGroup(*toDoGroups)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteToDoGroup(vararg toDoGroups: ToDoGroup) {
        toDoDao.deleteToDoGroup(*toDoGroups)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addToDoContacts(vararg toDoContacts: ToDoContacts) {
        toDoDao.addToDoContacts(*toDoContacts)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateToDoContacts(vararg toDoContacts: ToDoContacts) {
        toDoDao.updateToDoContacts(*toDoContacts)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteToDoContacts(vararg toDoContacts: ToDoContacts) {
        toDoDao.deleteToDoContacts(*toDoContacts)
    }

    // Abfragen

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteToDo(toDoId: Int) {
        toDoDao.deleteToDo(toDoId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllToDoGroups() {
        toDoDao.deleteAllToDoGroups()
    }

    /*
    // Eine Abfrage mit Rückgabe von Daten als Liste
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllToDos(): List<ToDoItem> {
        return toDoDao.getAllToDosAsFlow()
    }
    */

    // Flow und Observer

    // Für die Verwendung mit Flow und zur Nutzung mit einem Observer
    // ist dies Pattern notwendig. ACHTUNG: fun ohne suspend!
    fun getAllToDosAsFlow_DateThenImportance(): Flow<List<ToDoItem>> =
        toDoDao.getAllToDosAsFlow_DateThenImportance()

    fun getAllToDosAsFlow_ImportanceThenDate(): Flow<List<ToDoItem>> =
        toDoDao.getAllToDosAsFlow_ImportanceThenDate()

    // Zusätzliche Funktionalität

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun emptyLokalDatabase() {
        toDoDao.deleteAllToDoItems()
        toDoDao.deleteAllToDoGroups()
        toDoDao.deleteAllToDoContacts()

        ensureDefaultToDoGroup()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun emptyRemoteDatabase() {
        val api = FirestoreApi()
        api.emptyStore()
        api.ensureDefaultGroup()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun RefreshDatabase() {
        emptyLokalDatabase()
        ensureDefaultToDoGroup()
        createSampleEntities()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun ensureDefaultToDoGroup(): Long {
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

        return id
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun createSampleEntities() {
        RepositoryHelper(toDoDao).createSampleEntities(defaultGroup)
    }
}