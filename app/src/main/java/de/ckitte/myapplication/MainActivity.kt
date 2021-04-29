package de.ckitte.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.entities.ToDoGroup
import de.ckitte.myapplication.database.repository.ToDoRepository
import kotlinx.coroutines.launch
import java.io.Console
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db: ToDoDao = ToDoDatabase.getInstance(this).toToDao

        lifecycleScope.launch {
            ToDoRepository(db).emptyDatabase()
            ToDoRepository(db).ensureDefaultGroup()

            val toDo = ToDo(
                0,
                "Erstes ToDo",
                "Mein erstes ToDo",
                false,
                false,
                LocalDateTime.now(),
                ToDoRepository.defaultGroup)
            db.addUser(toDo)
        }
    }
}