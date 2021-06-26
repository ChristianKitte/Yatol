package de.ckitte.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.ckitte.myapplication.database.converters.DateConverter
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.LocalToDo
import de.ckitte.myapplication.database.entities.LocalToDoContact

/**
 * Die lokale SQLite Datenbank der Anwendung in Form einer RoomDatabase.
 * Alle Datenbankoperationen von der Erstellung bis zur Restrukturierung werden
 * mit Hilfe der Room Bibliothek umgesetzt.
 * @property toToDao Das zu verwendende DataAcessObject Interface vom Typ [ToDoDao] für den Zugriff
 */
@Database(
    entities = [
        LocalToDo::class,
        LocalToDoContact::class
    ],
    exportSchema = false,
    version = 31
)
@TypeConverters(DateConverter::class)
abstract class ToDoDatabase : RoomDatabase() {
    abstract val toToDao: ToDoDao

    companion object {
        /**
         * Ein Singleton, welche eine Instanz der Datenbank repräsentiert
         */
        @Volatile
        private var instance: ToDoDatabase? = null

        /**
         * Erzeugt ein Objekt der Datenbank als Singleton. Hierbei wird bei der Restruktrierung ein
         * destruktives Vorgehen angewendet (im Falle eines neuen Datenbank Schemas wird die zugrundliegende
         * Datenbank ggf. zerstört werden).
         * @param context Context Der Context, in dem die Nutzung erfolgen soll
         * @return ToDoDatabase Die von der Room Bibliothek verwaltete Datenbank
         */
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
