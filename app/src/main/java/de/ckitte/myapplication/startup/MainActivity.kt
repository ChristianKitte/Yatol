package de.ckitte.myapplication.startup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.repository.ToDoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val applicationScope = CoroutineScope(SupervisorJob())
        val db: ToDoDao = ToDoDatabase.getInstance(this, applicationScope).toToDao

        GlobalScope.launch {
            ToDoRepository(db).emptyDatabase()
            ToDoRepository(db).ensureDefaultToDoGroup()
            ToDoRepository(db).createSampleEntities()
        }
    }
}