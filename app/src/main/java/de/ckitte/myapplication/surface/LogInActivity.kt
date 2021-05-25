package de.ckitte.myapplication.surface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import de.ckitte.myapplication.R
import de.ckitte.myapplication.database.ToDoDatabase
import de.ckitte.myapplication.database.repository.ToDoRepository
import de.ckitte.myapplication.databinding.ActivityLogInBinding
import de.ckitte.myapplication.databinding.ActivityMainBinding
import de.ckitte.myapplication.login.LoginProvider
import de.ckitte.myapplication.login.LoginProvider.Companion.ValidateCredentials
import de.ckitte.myapplication.startup.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LogInActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLogInBinding
    private var isValidInput: Boolean = false

    private val user: String = "yattol@hallo.ms"
    private val key: String = "01234"

    // https://miromatech.com/android/edittext-inputtype/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this._binding = ActivityLogInBinding.inflate(layoutInflater)

        val view = _binding.root
        setContentView(view)



        _binding.etUsername.addTextChangedListener {
            validateForm()
        }

        _binding.etPassword.addTextChangedListener {
            validateForm()
        }

        _binding.btnLogin.setOnClickListener {
            if (isValidInput) {
                _binding.etUsername.setText("yattol@hallo.ms")
                _binding.etPassword.setText("01234")

                val myCredentials = LoginProvider.Companion.YATOLCredentials(
                    user = _binding.etUsername.text.toString(),
                    key = _binding.etPassword.text.toString()
                )

                val isValid = ValidateCredentials(myCredentials)

                if (isValid) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    wipeInput()

                    Snackbar.make(
                        view,
                        "Der Benutzername oder das Passwort sind falsch",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun wipeInput() {
        _binding.apply {
            etUsername.text.clear()
            etPassword.text.clear()
        }
    }

    private fun validateForm() {
        isValidInput = true

        _binding.btnLogin.isEnabled = isValidInput
    }
}