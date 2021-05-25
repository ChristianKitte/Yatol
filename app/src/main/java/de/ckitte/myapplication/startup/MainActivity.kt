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
import de.ckitte.myapplication.util.ConnectionLiveData
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var db: ToDoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this._binding = ActivityMainBinding.inflate(layoutInflater)

        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this, {
            if (it) {
                this.title = "YATOL - Verbunden"
            } else {
                this.title = "YATOL - Kein Netzwerk"
            }
        })

        val view = _binding.root
        setContentView(view)

        val applicationScope = CoroutineScope(SupervisorJob())
        this.db = ToDoDatabase.getInstance(this, applicationScope).toToDao

        GlobalScope.launch {
            ToDoRepository(db).RefreshDatabase()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater

        inflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // https://developer.android.com/guide/topics/ui/menus

        return when (item.itemId) {
            R.id.miCleanLokal -> {
                GlobalScope.launch {
                    ToDoRepository(db).emptyLokalDatabase()
                }

                Toast.makeText(
                    applicationContext,
                    "Die lokalen Daten werden gelöscht",
                    Toast.LENGTH_SHORT
                ).show()

                true
            }
            R.id.miCleanRemote -> {

                Toast.makeText(
                    applicationContext,
                    "miCleanRemote - Die Remotedaten werden gleöscht",
                    Toast.LENGTH_SHORT
                ).show()

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}