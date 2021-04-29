package de.ckitte.myapplication.database.repository

import androidx.annotation.WorkerThread
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDoGroup

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
        val todogroup = ToDoGroup(0, "Default", "Alle Einträge ohne Gruppe")
        val id = toDoDao.addGroup(todogroup)
        ToDoRepository.defaultGroup = id
        return id
    }
}