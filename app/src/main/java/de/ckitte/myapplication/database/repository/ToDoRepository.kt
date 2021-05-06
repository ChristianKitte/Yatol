package de.ckitte.myapplication.database.repository

import androidx.annotation.WorkerThread
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.entities.ToDoGroup
import kotlinx.coroutines.flow.Flow

class ToDoRepository(private val toDoDao: ToDoDao) {
    companion object {
        var defaultGroup: Long = 0
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun emptyDatabase() {
        toDoDao.deleteAllToDos()
        toDoDao.deleteAllToDoGroups()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun ensureDefaultGroup(): Long {
        val todogroup = ToDoGroup(0, true, "Default", "Alle Einträge ohne Gruppe")
        val id = toDoDao.addGroup(todogroup)
        ToDoRepository.defaultGroup = id
        return id
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addToDo(vararg toDos: ToDo) {
        toDoDao.addToDo(*toDos)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateToDo(vararg toDos: ToDo) {
        toDoDao.addToDo(*toDos)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteToDo(vararg toDos: ToDo) {
        toDoDao.addToDo(*toDos)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addGroup(toDoGroup: ToDoGroup): Long {
        return toDoDao.addGroup(toDoGroup)
    }

    suspend fun addGroup(vararg toDoGroup: ToDoGroup) {
        toDoDao.addGroup(*toDoGroup)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateGroup(vararg toDoGroups: ToDoGroup) {
        toDoDao.updateGroup(*toDoGroups)
    }

    suspend fun deleteGroup(vararg toDoGroups: ToDoGroup) {
        toDoDao.deleteGroup(*toDoGroups)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllToDos() {
        toDoDao.deleteAllToDos()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllToDoGroups() {
        toDoDao.deleteAllToDoGroups()
    }

    // Query mit Datnrückgabe
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllToDos(): List<ToDo> {
        return toDoDao.getAllToDos()
    }

    //EIgenschaft für Observer
    fun allToDosFlow(): Flow<List<ToDo>> = toDoDao.getAllToDosFlow()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun createSampleEntities() {
        RepositoryHelper(toDoDao).createSampleEntities()
    }
}