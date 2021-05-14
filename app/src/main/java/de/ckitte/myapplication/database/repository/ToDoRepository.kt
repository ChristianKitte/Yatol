package de.ckitte.myapplication.database.repository

import android.view.inspector.InspectionCompanion
import androidx.annotation.WorkerThread
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.entities.ToDoGroup
import kotlinx.coroutines.flow.Flow

class ToDoRepository(private val toDoDao: ToDoDao) {
    // Statische Eigenschaften

    companion object StaticMembers {
        var defaultGroup: Long = 0
    }

    // CRUD ToDoItem

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addToDoItem(vararg toDos: ToDoItem) {
        toDoDao.addToDoItem(*toDos)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateToDoItem(vararg toDos: ToDoItem) {
        toDoDao.addToDoItem(*toDos)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteToDoItem(vararg toDos: ToDoItem) {
        toDoDao.addToDoItem(*toDos)
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

    // Eine Abfrage mit Rückgabe von Daten als Liste
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllToDos(): List<ToDoItem> {
        return toDoDao.getAllToDos()
    }

    // Flow und Observer

    // Für die Verwendung mit Flow und zur Nutzung mit einem Observer
    // ist dies Pattern notwendig. ACHTUNG: fun ohne suspend!
    fun getAllToDosAsFlow(): Flow<List<ToDoItem>> = toDoDao.getAllToDosAsFlow()

    // Zusätzliche Funktionalität

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun emptyLokalDatabase() {
        toDoDao.deleteAllToDoItems()
        toDoDao.deleteAllToDoGroups()
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