package de.ckitte.myapplication.startup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.databinding.ActivityMainBinding
import de.ckitte.myapplication.login.LoginProvider
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
                //this.title = "YATOL - Verbunden"

                when (LoginProvider.isLoggedIn()) {
                    true -> {
                        configureActionBar("YATOL - Verbunden", "Logged In")
                    }
                    false -> {
                        configureActionBar("YATOL - Verbunden", "Logged Out")
                    }
                }
            } else {
                //this.title = "YATOL - Kein Netzwerk"

                when (LoginProvider.isLoggedIn()) {
                    true -> {
                        configureActionBar("YATOL - Kein Netzwerk", "Logged In")
                    }
                    false -> {
                        configureActionBar("YATOL - Kein Netzwerk", "Logged Out")
                    }
                }
            }
        })

        val view = _binding.root
        setContentView(view)

        val applicationScope = CoroutineScope(SupervisorJob())
        this.db = ToDoDatabase.getInstance(this).toToDao

        checkPermission(Manifest.permission.READ_CONTACTS, 100)
        checkPermission(Manifest.permission.CALL_PHONE, 110)
    }

    private fun configureActionBar(titel: String, subtitle: String) {
        val bar = supportActionBar

        bar?.let {
            it.title = titel
            it.subtitle = subtitle

            //it.setIcon(R.drawable.ic_edit)

            it.setDisplayUseLogoEnabled(false)
            it.setDisplayShowHomeEnabled(true)
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
                showToast("Starte das Löschen der lokalen Daten")

                CoroutineScope(Dispatchers.IO).launch {
                    ToDoRepository(db).emptyLokalDatabase()
                    withContext(Dispatchers.Main) {
                        showToast("Die lokalen Daten wurden gelöscht")
                    }
                }

                true
            }
            R.id.miCleanRemote -> {
                if (ConnectionLiveData.isConnected && LoginProvider.isLoggedIn()) {
                    showToast("Starte das Löschen der Remotedaten")

                    CoroutineScope(Dispatchers.IO).launch {

                        ToDoRepository(db).emptyRemoteDatabase()
                        withContext(Dispatchers.Main) {
                            showToast("Die Remotedaten wurden gelöscht")
                        }
                    }
                } else {
                    if (!ConnectionLiveData.isConnected) {
                        showToast("Kein Netzwerk - Die Remotedaten können nicht gelöscht werden")
                    } else if (!LoginProvider.isLoggedIn()) {
                        showToast("Nicht eingelogged - Die Remotedaten können nicht gelöscht werden")
                    }
                }

                true
            }
            R.id.miMirrorToRemote -> {
                if (ConnectionLiveData.isConnected && LoginProvider.isLoggedIn()) {
                    showToast("Die lokalen Daten werden in Firestore gesichert")

                    CoroutineScope(Dispatchers.IO).launch {
                        ToDoRepository(db).mirrorLocalToRemote()
                        withContext(Dispatchers.Main) {
                            showToast("Die lokalen Daten wurden in Firestore gesichert")
                        }
                    }


                } else {
                    if (!ConnectionLiveData.isConnected) {
                        showToast("Kein Netzwerk - Die lokalen Daten können nicht in Firestore gesichert werden")
                    } else if (!LoginProvider.isLoggedIn()) {
                        showToast("Nicht eingelogged - Die lokalen Daten können nicht in Firestore gesichert werden")
                    }
                }

                true
            }
            R.id.miMirrorFromRemote -> {
                if (ConnectionLiveData.isConnected && LoginProvider.isLoggedIn()) {
                    showToast("Daten werden aus Firestore geladen")

                    CoroutineScope(Dispatchers.IO).launch {
                        ToDoRepository(db).mirrorRemoteToLocal()
                        withContext(Dispatchers.Main) {
                            showToast("Die Daten wurden aus Firestore geladen")
                        }
                    }


                } else {
                    if (!ConnectionLiveData.isConnected) {
                        showToast("Kein Netzwerk - Daten können nicht aus Firestore geladen werden")
                    } else if (!LoginProvider.isLoggedIn()) {
                        showToast("Nicht eingelogged - Daten können nicht aus Firestore geladen werden")
                    }
                }

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            applicationContext,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }
}