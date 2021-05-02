package de.ckitte.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.entities.ToDo
import de.ckitte.myapplication.database.repository.ToDoRepository

class MainActivity : AppCompatActivity() {
    private var MeineListe: List<ToDo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db: ToDoDao = ToDoDatabase.getInstance(this).toToDao
        //var rep = ToDoRepository(db)
        //var factory = MainViewModelFactory(rep)

        //var testmodel: MainViewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        //testmodel.allToDos()

        suspend fun initializeDatabase() {
            ToDoRepository(db).emptyDatabase()
            ToDoRepository(db).ensureDefaultGroup()
            ToDoRepository(db).createSampleEntities()
        }

    }

    //#https://agrawalsuneet.github.io/blogs/variable-number-of-arguments-vararg-kotlin/
}