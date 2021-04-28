package de.ckitte.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.entities.ToDoGroup
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db: ToDoDao = ToDoDatabase.getInstance(this).toToDao

        val toDo = ToDo(0, "Erstes ToDo", "Mein erstes ToDo", false, false, LocalDateTime.now(), 0)
        val todogroup = ToDoGroup(0, "Default", "Alle Einträge ohne Zuordnung zu einer Gruppe")

        lifecycleScope.launch {
            var d=db.addGroup(todogroup)
            db.addUser(toDo)
        }
    }
}