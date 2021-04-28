package de.ckitte.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.ckitte.myapplication.database.converters.DateConverter
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.entities.ToDoGroup

@Database(
    entities = [
        ToDo::class,
        ToDoGroup::class
    ],
    version = 2
)
@TypeConverters(DateConverter::class)
abstract class ToDoDatabase : RoomDatabase() {
    abstract val toToDao: ToDoDao

    companion object {
        @Volatile
        private var instance: ToDoDatabase? = null

        fun getInstance(context: Context): ToDoDatabase {
            synchronized(this) {
                return instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "toDoDatabase"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }

    }
}