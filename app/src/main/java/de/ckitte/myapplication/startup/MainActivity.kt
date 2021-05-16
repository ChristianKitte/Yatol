package de.ckitte.myapplication.startup

import android.content.Context
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.AttributeSet
import android.view.*
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.ContentView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.solver.widgets.analyzer.Dependency
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var db: ToDoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this._binding = ActivityMainBinding.inflate(layoutInflater)

        val view = _binding.root
        setContentView(view)

        val applicationScope = CoroutineScope(SupervisorJob())
        this.db = ToDoDatabase.getInstance(this, applicationScope).toToDao

        _binding.menuBottomNavigation.menu.getItem(2).isEnabled = false
        _binding.menuBottomNavigation.menu.getItem(3).isEnabled = false
        _binding.menuBottomNavigation.menu.getItem(4).isEnabled = false

        _binding.fabAdd.setOnClickListener {
            Toast.makeText(
                applicationContext,
                "Ein neues ToDo Item anlegen",
                Toast.LENGTH_SHORT
            ).show()
            //this.findNavController(R.id.nav_host_fragment_container).navigate(R.id.action_toDoListFragment_to_addToDo)
        }

        _binding.menuBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miClose -> {
                    finish()
                }
                R.id.miRefresh -> {
                    GlobalScope.launch {
                        ToDoRepository(db).RefreshDatabase()
                    }
                }
            }

            true
        }

        GlobalScope.launch {
            ToDoRepository(db).RefreshDatabase()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater

        inflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miCleanLokal -> {
                GlobalScope.launch {
                    ToDoRepository(db).emptyLokalDatabase()
                }

                val toast =
                    Toast.makeText(
                        applicationContext,
                        "Die lokalen Daten werden gelöscht",
                        Toast.LENGTH_SHORT
                    ).show()

                return true
            }
            R.id.miCleanRemote -> {
                val toast =
                    Toast.makeText(
                        applicationContext,
                        "miCleanRemote - Die Remotedaten werden gleöscht",
                        Toast.LENGTH_SHORT
                    ).show()

                return true
            }
            R.id.miDoneFirst -> {
                val toast =
                    Toast.makeText(
                        applicationContext,
                        "miDoneFirst - Die Sortierung wurde geändert",
                        Toast.LENGTH_SHORT
                    ).show()

                return true
            }
            R.id.miNotDoneFirst -> {
                val toast =
                    Toast.makeText(
                        applicationContext,
                        "miNotDoneFirst - Die Sortierung wurde geändert",
                        Toast.LENGTH_SHORT
                    ).show()

                return true
            }
            R.id.miDateFirst -> {
                val toast =
                    Toast.makeText(
                        applicationContext,
                        "miDateFirst - Die Sortierung wurde geändert",
                        Toast.LENGTH_SHORT
                    ).show()

                return true
            }
            R.id.miPriorityFirst -> {
                val toast =
                    Toast.makeText(
                        applicationContext,
                        "miPriorityFirst - Die Sortierung wurde geändert",
                        Toast.LENGTH_SHORT
                    ).show()

                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
                return false
            }
        }
    }
}