package de.ckitte.myapplication.database

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.ckitte.myapplication.database.converters.DateConverter
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.entities.ToDoGroup
import de.ckitte.myapplication.database.repository.ToDoRepository
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.nio.file.Path
import java.time.LocalDateTime
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ToDo::class,
        ToDoGroup::class
    ],
    exportSchema = false,
    version = 2
)
@TypeConverters(DateConverter::class)
abstract class ToDoDatabase : RoomDatabase() {
    abstract val toToDao: ToDoDao

    companion object {
        @Volatile
        private var instancex: ToDoDatabase? = null

        fun getInstance(
            context: Context,
            scope: CoroutineScope
        ): ToDoDatabase {
            return instancex ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "toDoDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(ToDoDatabaseCallback(scope))
                    .build()
                instancex = instance
                return instance
            }

        }

        private class ToDoDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                // If you want to keep the data through app restarts,
                // comment out the following line.
                instancex?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        //populateDatabase(database.toToDao)
                    }
                }
            }
        }

        suspend fun populateDatabase(toDoDao: ToDoDao) {
            toDoDao.addToDo(
                ToDo(
                    0,
                    "Sample 1",
                    "toDo Sample 1",
                    false,
                    true,
                    LocalDateTime.now(),
                    ToDoRepository.defaultGroup
                ),
                ToDo(
                    0,
                    "Sample 2",
                    "toDo Sample 2",
                    true,
                    false,
                    LocalDateTime.now().plusDays(1),
                    ToDoRepository.defaultGroup
                ),
                ToDo(
                    0,
                    "Sample 3",
                    "toDo Sample 3",
                    false,
                    true,
                    LocalDateTime.now().plusDays(2).plusHours(3),
                    ToDoRepository.defaultGroup
                ),
                ToDo(
                    0,
                    "Sample 4",
                    "toDo Sample 4",
                    true,
                    false,
                    LocalDateTime.now().plusDays(3),
                    ToDoRepository.defaultGroup
                ),
                ToDo(
                    0,
                    "Sample 5",
                    "toDo Sample 5",
                    false,
                    true,
                    LocalDateTime.now().plusDays(3).plusHours(3),
                    ToDoRepository.defaultGroup
                ),
                ToDo(
                    0,
                    "Sample 6",
                    "toDo Sample 6",
                    true,
                    false,
                    LocalDateTime.now().plusDays(2),
                    ToDoRepository.defaultGroup
                ),
                ToDo(
                    0,
                    "Sample 7",
                    "toDo Sample 7",
                    false,
                    true,
                    LocalDateTime.now(),
                    ToDoRepository.defaultGroup
                )
            )
        }
    }
}
