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

class LogInActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLoginBinding
    private lateinit var connectionLiveData: ConnectionLiveData
    private lateinit var db: ToDoDao

    // https://miromatech.com/android/edittext-inputtype/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this._binding = ActivityLoginBinding.inflate(layoutInflater)

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

                startApplication(false)
            }
        })

        val view = _binding.root
        setContentView(view)

        //val applicationScope = CoroutineScope(SupervisorJob())
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

            var isValid = false
            CoroutineScope(Dispatchers.IO).launch {
                isValid = LoginProvider.LogIn(myCredentials)

                withContext(Dispatchers.Main) {
                    loginResultHandler(isValid)
                }
            }
        }
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

    // https://riptutorial.com/android/example/17590/clear-your-current-activity-stack-and-launch-a-new-activity
    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        //_binding.progBar.visibility = View.INVISIBLE
        finishAffinity()
    }

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

    private fun wipeInput() {
        _binding.apply {
            etEmail.text.clear()
            etPassword.text.clear()
        }
    }

    private fun validateForm() {
        _binding.apply {
            btnLogin.isEnabled = etPassword.length() > 0
                    && etEmail.length() > 0
                    && EmailUtil.isValidEmail(etEmail.text.toString()) == true
        }
    }
}