package de.ckitte.myapplication.startup

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import de.ckitte.myapplication.databinding.ActivityLoginBinding
import de.ckitte.myapplication.login.LoginProvider
import de.ckitte.myapplication.util.ConnectionLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogInActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLoginBinding
    private lateinit var connectionLiveData: ConnectionLiveData

    // https://miromatech.com/android/edittext-inputtype/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this._binding = ActivityLoginBinding.inflate(layoutInflater)

        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this, {
            if (it) {
                this.title = "YATOL - Verbunden"
            } else {
                _binding.progBar.visibility = View.VISIBLE
                this.title = "YATOL - Kein Netzwerk"
                openMainActivity()
            }
        })

        val view = _binding.root
        setContentView(view)

        _binding.etEmail.addTextChangedListener {
            _binding.tvEmailHint.isVisible = false
            validateForm()
        }

        _binding.etPassword.addTextChangedListener {
            validateForm()
        }

        _binding.etEmail.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                _binding.tvEmailHint.isVisible = !isValidEmail(_binding.etEmail.text.toString())
            }
        }

        _binding.btnLogin.setOnClickListener {
            _binding.progBar.visibility = View.VISIBLE

            val myCredentials = LoginProvider.Companion.YATOLMailCredentials(
                user = _binding.etEmail.text.toString(),
                key = _binding.etPassword.text.toString()
            )

            var isValid = false
            GlobalScope.launch {
                isValid = LoginProvider.Companion.LogIn(myCredentials)

                withContext(Dispatchers.Main) {
                    _binding.progBar.visibility = View.INVISIBLE
                    loginResultHandler(isValid)
                }
            }
        }
    }

    // https://riptutorial.com/android/example/17590/clear-your-current-activity-stack-and-launch-a-new-activity
    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)
        finishAffinity()
    }

    private fun loginResultHandler(isValid: Boolean) {
        if (isValid) {
            openMainActivity()
        } else {
            wipeInput()

            Snackbar.make(
                _binding.root,
                "Der Benutzername oder das Passwort sind falsch !",
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
        return testMail
    }

    private fun validateForm() {
        _binding.apply {
            btnLogin.isEnabled = etPassword.length() > 0
                    && etEmail.length() > 0
                    && isValidEmail(etEmail.text.toString()) == true
        }
    }
}