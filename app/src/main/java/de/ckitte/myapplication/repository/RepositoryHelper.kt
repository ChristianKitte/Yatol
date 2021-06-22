package de.ckitte.myapplication.repository

import androidx.annotation.WorkerThread
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.LokalToDo
import java.time.LocalDateTime

class RepositoryHelper(private val toDoDao: ToDoDao) {
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun createSampleEntities() {
        toDoDao.addLocalToDo(
            LokalToDo(
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
            LokalToDo(
                0,
                "",
                "Sample 2",
                "toDo Sample 2wwwwwwwwwwwwwwwwwwwwwwwwwwwwwoooooooooooooooooooooooooooooooooooppppppppppppppppppppppppppp",
                true,
                false,
                LocalDateTime.now().plusDays(1)
            )
        )

        toDoDao.addLocalToDo(
            LokalToDo(
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
            LokalToDo(
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
            LokalToDo(
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
            LokalToDo(
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
            LokalToDo(
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