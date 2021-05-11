package de.ckitte.myapplication.startup

import android.content.Context
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.AttributeSet
import android.view.*
import android.widget.Toast
import androidx.annotation.ContentView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var db: ToDoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        _binding.menuBottomNavigation.menu.getItem(2).isEnabled = false

        val applicationScope = CoroutineScope(SupervisorJob())
        this.db = ToDoDatabase.getInstance(this, applicationScope).toToDao

        GlobalScope.launch {
            ToDoRepository(db).emptyDatabase()
            ToDoRepository(db).ensureDefaultToDoGroup()
            ToDoRepository(db).createSampleEntities()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miCleanLokal -> {
                val toast =
                    Toast.makeText(applicationContext, "Die lokalen Daten wurden gelöscht", Toast.LENGTH_SHORT).show()
                GlobalScope.launch {
                    ToDoRepository(db).emptyDatabase()
                    ToDoRepository(db).ensureDefaultToDoGroup()
                }
                return true
            }
            R.id.miCleanRemote -> {
                val toast =
                    Toast.makeText(applicationContext, "miCleanRemote", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.miRefresh -> {
                val toast =
                    Toast.makeText(applicationContext, "miRefresh", Toast.LENGTH_SHORT).show()
                GlobalScope.launch {
                    ToDoRepository(db).emptyDatabase()
                    ToDoRepository(db).ensureDefaultToDoGroup()
                    ToDoRepository(db).createSampleEntities()
                }
                return true
            }
            R.id.miDoneFirst -> {
                val toast =
                    Toast.makeText(applicationContext, "miDoneFirst", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.miNotDoneFirst -> {
                val toast =
                    Toast.makeText(applicationContext, "miNotDoneFirst", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.miDateFirst -> {
                val toast =
                    Toast.makeText(applicationContext, "miDateFirst", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.miPriorityFirst -> {
                val toast =
                    Toast.makeText(applicationContext, "miPriorityFirst", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.miClose -> {
                val toast =
                    Toast.makeText(applicationContext, "miClose", Toast.LENGTH_SHORT).show()
                return true
            }

            else -> {
                super.onOptionsItemSelected(item)
                return false
            }
        }
    }
}