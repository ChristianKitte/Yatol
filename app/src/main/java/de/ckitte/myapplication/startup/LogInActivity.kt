package de.ckitte.myapplication.startup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.daos.ToDoDao
import de.ckitte.myapplication.databinding.ActivityLoginBinding
import de.ckitte.myapplication.login.LoginProvider
import de.ckitte.myapplication.repository.ToDoRepository
import de.ckitte.myapplication.util.ConnectionLiveData
import de.ckitte.myapplication.util.EmailUtil
import kotlinx.coroutines.*

/**
 * LogIn Aktivität. Bietet die Funktionalität für einen LogIn an
 * @property _binding ActivityLoginBinding Das zugehörige Binding Objekt
 * @property connectionLiveData ConnectionLiveData Eine Observer Objekt für die Netzverfügbarkeit
 * @property db ToDoDao Eine Instanz von [ToDoDao]
 */
class LogInActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLoginBinding
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var db: ToDoDao

    /**
     * Initialisiert die neue Instanz
     * @param savedInstanceState Bundle? Eine Instanz von Typ Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this._binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = _binding.root

        setContentView(view)

        // Definition des EventHandler als annonyme Methode
        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this, {
            if (it) {
                _binding.btnLogin.isEnabled = true

                when (LoginProvider.isLoggedIn()) {
                    true -> {
                        configureActionBar("YATOL - Verbunden", "Logged In")
                    }
                    false -> {
                        configureActionBar("YATOL - Verbunden", "Logged Out")
                    }
                }
            } else {
                _binding.btnLogin.isEnabled = false

                when (LoginProvider.isLoggedIn()) {
                    true -> {
                        configureActionBar("YATOL - Kein Netzwerk", "Logged In")
                    }
                    false -> {
                        configureActionBar("YATOL - Kein Netzwerk", "Logged Out")
                    }
                }

                startApplication(false)
            }
        })

        this.db = ToDoDatabase.getInstance(this).toToDao

        _binding.etEmail.addTextChangedListener {
            _binding.tvEmailHint.isVisible = false
            validateForm()
        }

        _binding.etPassword.addTextChangedListener {
            validateForm()
        }

        _binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                _binding.tvEmailHint.isVisible =
                    !EmailUtil.isValidEmail(_binding.etEmail.text.toString())
            }
        }

        _binding.btnLogin.setOnClickListener {
            _binding.progBar.visibility = View.VISIBLE

            val myCredentials = LoginProvider.Companion.YATOLMailCredentials(
                user = _binding.etEmail.text.toString(),
                key = _binding.etPassword.text.toString()
            )

            var isValid: Boolean
            CoroutineScope(Dispatchers.IO).launch {
                isValid = LoginProvider.LogIn(myCredentials)

                withContext(Dispatchers.Main) {
                    loginResultHandler(isValid)
                }
            }
        }
    }

    //region Start der Anwendung

    /**
     * Führt den Start der Anwendung durch und wechselt dann zu [MainActivity]
     * @param synchronize Boolean True, wenn vorher eine Synchronisierung durchgeführt werden soll, sonst False
     */
    private fun startApplication(synchronize: Boolean) {
        if (synchronize) {
            CoroutineScope(Dispatchers.IO).launch {
                ToDoRepository(db).refreshDatabase()
                withContext(Dispatchers.Main) {
                    openMainActivity()
                }
            }
        } else {
            openMainActivity()
        }
    }

    /**
     * Führt den eigentlcihen Wechsel zu [MainActivity] aus
     *
     * https://riptutorial.com/android/example/17590/clear-your-current-activity-stack-and-launch-a-new-activity
     */
    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    //endregion

    //region Oberflächen und Interaktion

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

    /**
     * Startet die Hauptanwendung oder informiert über den Fehlversuch in Abhängigkeit des Ergebnisses des Logins
     * @param isValid Boolean Das Ergbenis der Prüfung
     */
    private fun loginResultHandler(isValid: Boolean) {
        if (isValid) {
            startApplication(true)
        } else {
            _binding.progBar.visibility = View.INVISIBLE

            wipeInput()

            Snackbar.make(
                _binding.root,
                "Der Benutzername oder das Passwort sind falsch !",
                Snackbar.LENGTH_INDEFINITE
            ).show()
        }
    }

    /**
     * Löscht die EIngabefelder für eMail und Passwort
     */
    private fun wipeInput() {
        _binding.apply {
            etEmail.text.clear()
            etPassword.text.clear()
        }
    }

    /**
     * Prüft formale Kriterien der Eingabe der eMail Adresse. Wenn alle Voraussetzungen erfüllt
     * sind, wird der Login Button enabled.
     */
    private fun validateForm() {
        _binding.apply {
            btnLogin.isEnabled = etPassword.length() > 0
                    && etEmail.length() > 0
                    && EmailUtil.isValidEmail(etEmail.text.toString()) == true
        }
    }

    //endregion
}