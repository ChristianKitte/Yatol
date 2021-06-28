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
import de.ckitte.myapplication.databinding.ActivityMainBinding
import de.ckitte.myapplication.login.LoginProvider
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.util.ConnectionLiveData
import kotlinx.coroutines.*


/**
 * Hauptfenster der Anwendung
 * @property _binding ActivityMainBinding Das zugehörige Binding Objekt
 * @property connectionLiveData ConnectionLiveData Eine Observer Objekt für die Netzverfügbarkeit
 * @property db ToDoDao Eine Instanz von [ToDoDao]
 */
class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var db: ToDoDao

    /**
     * Initialisiert die neue Instanz
     * @param savedInstanceState Bundle? Eine Instanz von Typ Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this._binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root

        setContentView(view)

        // Definition des EventHandler als annonyme Methode
        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this, {
            if (it) {
                when (LoginProvider.isLoggedIn()) {
                    true -> {
                        configureActionBar("YATOL - Verbunden", "Logged In")
                    }
                    false -> {
                        configureActionBar("YATOL - Verbunden", "Logged Out")
                    }
                }
            } else {
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

        this.db = ToDoDatabase.getInstance(this).toToDao

        // Einholen von Erlaubnissen
        checkPermission(Manifest.permission.READ_CONTACTS, 100)
        checkPermission(Manifest.permission.CALL_PHONE, 110)
    }

    //region Actionbar

    /**
     * Setzt die Ausgabe der oberen Action Bar
     * @param titel String Der Titel der Ausgabe
     * @param subtitle String Der Untertitel der Ausgabe
     */
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

    //endregion

    //region Optionsmenü

    /**
     * Erstellen des Optionsmenüs
     * @param menu Menu Das Menü
     * @return Boolean True, wenn alles in Ordnung ist, sonst False
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater

        inflater.inflate(R.menu.menu_main, menu)

        return true
    }

    /**
     * Setzte die Verfügbarkeit des OptionMenüs in Abhängigkeit von der Netzverbindung.
     * Hierbei kann die Funktion trotzdessen fehlschlagen, sofern der Nutzer nicht eingelogged ist.
     * Dies wird dem Nutzer jedoch rückgemeldet.
     * @param menu Eine Referenz auf OptionMenü
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            menu.findItem(R.id.miCleanRemote).isEnabled = ConnectionLiveData.isConnected
            menu.findItem(R.id.miMirrorToRemote).isEnabled = ConnectionLiveData.isConnected
            menu.findItem(R.id.miMirrorFromRemote).isEnabled = ConnectionLiveData.isConnected
        }

        return true
    }

    /**
     * Handler für das Optionsmenü oben rechts
     * @param item MenuItem Das Menü
     * @return Boolean True, wenn alles in Ordnung ist, sonst False
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
            R.id.miDeleteDone -> {
                if (!ConnectionLiveData.isConnected || !LoginProvider.isLoggedIn()) {
                    if (!ConnectionLiveData.isConnected) {
                        showToast("Kein Netzwerk - Die Daten werden nicht aus Firestore entfernt werden ")
                    } else if (!LoginProvider.isLoggedIn()) {
                        showToast("Nicht eingelogged - Die Daten werden nicht aus Firestore entfernt werden")
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    ToDoRepository(db).deleteDoneToDoItems()
                    withContext(Dispatchers.Main) {
                        showToast("Die Daten wurden entfernt")
                    }
                }

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    //endregion

    //region Hilfsfunktionen

    /**
     * Hilfsfunktion zum vereinfachten Anzeigen eines Toasts
     * @param text String Der anzuzeigende Text
     */
    private fun showToast(text: String) {
        Toast.makeText(
            applicationContext,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    // Function to check and request permission.
    /**
     * Hilfsfunktion zum vereinfachten Anfordern von Erlaubnissen
     * @param permission String Die anzufordernde Erlaubnis als Text
     * @param requestCode Int Ein frei wählbarer Anfragecode
     */
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

    //endregion
}