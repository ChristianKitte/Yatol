package de.ckitte.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.repository.ToDoRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db: ToDoDao = ToDoDatabase.getInstance(this).toToDao

        GlobalScope.launch {
            ToDoRepository(db).emptyDatabase()
            ToDoRepository(db).ensureDefaultGroup()
            ToDoRepository(db).createSampleEntities()
        }
    }
}