package de.ckitte.myapplication.startup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import de.ckitte.myapplication.databinding.ActivityLoginBinding
import de.ckitte.myapplication.login.LoginProvider
import de.ckitte.myapplication.login.LoginProvider.Companion.ValidateCredentials
import de.ckitte.myapplication.util.ConnectionLiveData
import kotlinx.coroutines.*

class LogInActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLoginBinding
    private var isValidInput: Boolean = false

    private lateinit var connectionLiveData: ConnectionLiveData

    // https://miromatech.com/android/edittext-inputtype/

    fun test(x: Boolean) {
        _binding.etEmail.setText(x.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this._binding = ActivityLoginBinding.inflate(layoutInflater)

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

        _binding.etEmail.addTextChangedListener {
            _binding.tvEmailHint.isVisible = false
            validateForm()
        }

        _binding.etEmail.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                _binding.tvEmailHint.isVisible = isValidEmail(_binding.etEmail.text.toString())
            }
        }
        _binding.etPassword.addTextChangedListener {
            validateForm()
        }

        _binding.btnLogin.setOnClickListener {
            if (isValidInput) {
                val myCredentials = LoginProvider.Companion.YATOLCredentials(
                    user = _binding.etEmail.text.toString(),
                    key = _binding.etPassword.text.toString()
                )

                var isValid = false
                GlobalScope.launch {
                    isValid = ValidateCredentials(myCredentials)
                    withContext(Dispatchers.Main) {
                        dorun(isValid)
                    }
                }


            }
        }
    }

    private fun dorun(isValid: Boolean) {
        if (isValid) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            wipeInput()

            Snackbar.make(
                _binding.root,
                "Der Benutzername oder das Passwort sind falsch",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun wipeInput() {
        _binding.apply {
            etEmail.text.clear()
            etPassword.text.clear()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val testMail = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return !testMail
    }

    private fun validateForm() {
        isValidInput = true

        _binding.btnLogin.isEnabled = isValidInput
    }
}