package de.ckitte.myapplication.repository

import androidx.annotation.WorkerThread
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.LocalToDo
import java.time.LocalDateTime

/**
 * Helferklasse für das Repository
 * @property toDoDao ToDoDao Die DAO Klasse der ROOM Bibliothek. Enthält Zugriffe zur SQLite DB via Room
 * @constructor
 */
class RepositoryHelper(private val toDoDao: ToDoDao) {
    /**
     * Erzeugt sieben Dummy Einträge eines [LocalToDo] und fügt sie der Datenbank hinzu
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun createSampleEntities() {
        toDoDao.addLocalToDo(
            LocalToDo(
                0,
                "",
                "Sample 1",
                "toDo Sample 1",
                false,
                true,
                LocalDateTime.now()
            )
        )

        toDoDao.addLocalToDo(
            LocalToDo(
                0,
                "",
                "Sample 2",
                "toDo Sample 2",
                true,
                false,
                LocalDateTime.now().plusDays(1)
            )
        )

        toDoDao.addLocalToDo(
            LocalToDo(
                0,
                "",
                "Sample 3",
                "toDo Sample 3",
                false,
                true,
                LocalDateTime.now().plusDays(2).plusHours(3)
            )
        )

        toDoDao.addLocalToDo(
            LocalToDo(
                0,
                "",
                "Sample 4",
                "toDo Sample 4",
                true,
                false,
                LocalDateTime.now().plusDays(3)
            )
        )

        toDoDao.addLocalToDo(
            LocalToDo(
                0,
                "",
                "Sample 5",
                "toDo Sample 5",
                false,
                true,
                LocalDateTime.now().plusDays(3).plusHours(3)
            )
        )

        toDoDao.addLocalToDo(
            LocalToDo(
                0,
                "",
                "Sample 6",
                "toDo Sample 6",
                true,
                false,
                LocalDateTime.now().plusDays(2)
            )
        )

        toDoDao.addLocalToDo(
            LocalToDo(
                0,
                "",
                "Sample 7",
                "toDo Sample 7",
                false,
                true,
                LocalDateTime.now()
            )
        )
    }
}