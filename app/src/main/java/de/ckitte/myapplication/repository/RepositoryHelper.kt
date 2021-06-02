package de.ckitte.myapplication.repository

import androidx.annotation.WorkerThread
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDoItem
import java.time.LocalDateTime

class RepositoryHelper(private val toDoDao: ToDoDao) {
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun createSampleEntities(defaultToDoGroupID: Long) {
        toDoDao.addToDoItem(
            ToDoItem(
                0,
                "",
                "Sample 1",
                "toDo Sample 1",
                false,
                true,
                LocalDateTime.now(),
                defaultToDoGroupID
            ),
            ToDoItem(
                0,
                "",
                "Sample 2",
                "toDo Sample 2wwwwwwwwwwwwwwwwwwwwwwwwwwwwwoooooooooooooooooooooooooooooooooooppppppppppppppppppppppppppp",
                true,
                false,
                LocalDateTime.now().plusDays(1),
                defaultToDoGroupID
            ),
            ToDoItem(
                0,
                "",
                "Sample 3",
                "toDo Sample 3",
                false,
                true,
                LocalDateTime.now().plusDays(2).plusHours(3),
                defaultToDoGroupID
            ),
            ToDoItem(
                0,
                "",
                "Sample 4",
                "toDo Sample 4",
                true,
                false,
                LocalDateTime.now().plusDays(3),
                defaultToDoGroupID
            ),
            ToDoItem(
                0,
                "",
                "Sample 5",
                "toDo Sample 5",
                false,
                true,
                LocalDateTime.now().plusDays(3).plusHours(3),
                defaultToDoGroupID
            ),
            ToDoItem(
                0,
                "",
                "Sample 6",
                "toDo Sample 6",
                true,
                false,
                LocalDateTime.now().plusDays(2),
                defaultToDoGroupID
            ),
            ToDoItem(
                0,
                "",
                "Sample 7",
                "toDo Sample 7",
                false,
                true,
                LocalDateTime.now(),
                defaultToDoGroupID
            )
        )
    }
}