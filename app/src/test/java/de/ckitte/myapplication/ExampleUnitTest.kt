package de.ckitte.myapplication

import de.ckitte.myapplication.login.LoginProvider
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LogInCredentialUnitTest {
    @Test
    fun login_credentialsTest() {
        assertEquals(
            false,
            LoginProvider.ValidateCredentials(LoginProvider.Companion.YATOLCredentials("", ""))
        )
        assertEquals(
            false,
            LoginProvider.ValidateCredentials(
                LoginProvider.Companion.YATOLCredentials(
                    "jhsjhjs@jkl ",
                    "234"
                )
            )
        )
        assertEquals(
            false,
            LoginProvider.ValidateCredentials(
                LoginProvider.Companion.YATOLCredentials(
                    "yattol @hallo.ms ",
                    "012345 "
                )
            )
        )
        assertEquals(
            true,
            LoginProvider.ValidateCredentials(
                LoginProvider.Companion.YATOLCredentials(
                    "yattol@hallo.ms",
                    "01234"
                )
            )
        )
        assertEquals(
            false,
            LoginProvider.ValidateCredentials(
                LoginProvider.Companion.YATOLCredentials(
                    "yattol@hallo.ms ",
                    "0 1234"
                )
            )
        )
    }
}
