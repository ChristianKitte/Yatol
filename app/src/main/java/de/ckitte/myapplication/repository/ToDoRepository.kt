package de.ckitte.myapplication.repository

import androidx.annotation.WorkerThread
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.entities.ToDoGroup
import kotlinx.coroutines.flow.Flow
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
        val x = de.ckitte.myapplication.firestore.FirestoreApi()

        val z = de.ckitte.myapplication.firestore.firestoreEntities.firestoreToDoItem(
            toDoId = "",
            toDoTitle = toDos[0].toDoTitle,
            toDoDescription = toDos[0].toDoDescription,
            toDoIsDone = toDos[0].toDoIsDone,
            toDoIsFavourite = toDos[0].toDoIsFavourite,
            toDoDoUntil = toDos[0].toDoDoUntil,
            toDoGroupId = "",
            user = ""
        )

        x.insertToDoItem("ToDoItems", z)
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
        val x = de.ckitte.myapplication.firestore.FirestoreApi()

        val z = de.ckitte.myapplication.firestore.firestoreEntities.firestoreToDoItem(
            toDoId = "V492NDgnClYz8byDtouj",
            toDoTitle = toDos[0].toDoTitle,
            toDoDescription = toDos[0].toDoDescription,
            toDoIsDone = toDos[0].toDoIsDone,
            toDoIsFavourite = toDos[0].toDoIsFavourite,
            toDoDoUntil = toDos[0].toDoDoUntil,
            toDoGroupId = "",
            user = ""
        )

        x.deleteToDoItem("ToDoItems", z)
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

    suspend fun addToDoGroup(vararg toDoGroup: ToDoGroup) {
        toDoDao.addToDoGroup(*toDoGroup)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateToDoGroup(vararg toDoGroups: ToDoGroup) {
        toDoDao.updateToDoGroup(*toDoGroups)
    }

    suspend fun deleteToDoGroup(vararg toDoGroups: ToDoGroup) {
        toDoDao.deleteToDoGroup(*toDoGroups)
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
        ensureDefaultToDoGroup()
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
        val todogroup = ToDoGroup(0, true, "Default", "Alle Einträge ohne Gruppe")
        val id = toDoDao.addToDoGroup(todogroup)

        ToDoRepository.defaultGroup = id

        return id
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun createSampleEntities() {
        RepositoryHelper(toDoDao).createSampleEntities(defaultGroup)
    }
}