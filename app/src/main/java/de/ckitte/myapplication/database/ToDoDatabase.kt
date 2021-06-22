package de.ckitte.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.ckitte.myapplication.database.converters.DateConverter
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDoItem
import de.ckitte.myapplication.database.entities.ToDoGroup
import de.ckitte.myapplication.database.entities.ToDoContact

@Database(
    entities = [
        ToDoItem::class,
        ToDoGroup::class,
        ToDoContact::class
    ],
    exportSchema = false,
    version = 17
)
@TypeConverters(DateConverter::class)
abstract class ToDoDatabase : RoomDatabase() {
    abstract val toToDao: ToDoDao

    companion object {
        @Volatile
        private var instance: ToDoDatabase? = null

        fun getInstance(
            context: Context
        ): ToDoDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "toDoDatabase"
                ).fallbackToDestructiveMigration()
                    .build()
                instance = newInstance
                return newInstance
            }
        }
    }
}
