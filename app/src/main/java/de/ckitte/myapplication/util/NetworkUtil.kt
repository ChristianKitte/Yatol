package de.ckitte.myapplication.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

/**
 * Implementiert ein LiveData Objekt, der die Netzverfügbarkeit überwacht und über Änderungen informiert
 *
 * Inspired by:
 *
 * https://www.youtube.com/watch?v=To9aHYD5OVk
 *
 * https://github.com/AlexSheva-mason/Rick-Morty-Database/blob/master/app/src/main/java/com/shevaalex/android/rickmortydatabase/utils/networking/ConnectionLiveData.kt
 *
 * @property networkCallback NetworkCallback Eine Callbackklasse für den [ConnectivityManager]
 * @property connectivityManeger ConnectivityManager Ein Systeminterner Verbindungsmanager
 * @property networkList MutableSet<Network> Eine Liste verfügbarer Netzwerke
 * @constructor
 */
class ConnectionLiveData(context: Context) : LiveData<Boolean>() {
    companion object {
        /**
         *
         */
        @Volatile
        var isConnected: Boolean = false
    }

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private val connectivityManeger =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkList: MutableSet<Network> = HashSet()

    //region Aktivierung und Inactivierung des Datenstroms handeln

    /**
     * Führt bei der Aktivierung eine Registrierung beim Conectivity Manager durch, um über
     * Netzwerkänderungen informiert zu werden.
     */
    override fun onActive() {
        networkCallback = createNetworkCallback()

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()

        connectivityManeger.registerNetworkCallback(networkRequest, networkCallback)

        if (connectivityManeger.activeNetwork == null) {
            checkValidNetworks()
        }
    }

    /**
     * Führt bei der Deaktivierung eine Deregistrierung beim Conectivity Manager durch
     */
    override fun onInactive() {
        connectivityManeger.unregisterNetworkCallback(networkCallback)
    }

    //endregion

    //region Callback erstellen

    /**
     * Eine Callback Klasse für die Information, dass sich die Netzwerksituation geändert hat.
     * @return <no name provided>
     */
    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        /**
         * Prüft und fügt ein neues oder wieder verfügbares Netzwerk an
         * https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onAvailable(android.net.Network)
         * @param network Network Eine Instanz vom Typ Network
         */
        override fun onAvailable(network: Network) {
            val networkCapabilities = connectivityManeger.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)

            if (hasInternetCapability == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (isInternet(network.socketFactory)) {
                        withContext(Dispatchers.Main) {
                            networkList.add(network)
                            checkValidNetworks()
                        }
                    }
                }
            }
        }

        /**
         * Löscht ein Netzwerk aus der List der verfügbaren Netzwerke, wenn es nicht mehr verfügbar ist.
         * Dies erfolgt nicht automatisch.
         * https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onLost(android.net.Network)
         * @param network Network
         */
        override fun onLost(network: Network) {
            networkList.remove(network)
            checkValidNetworks()
        }
    }

    //endregion

    //region Service Methoden

    /**
     * Prüft, ob ein Netzwerk verfügbar ist und veröffentlicht das Ergebnis. Gleichzeitig wird die
     * Eigenschaft [ConnectionLiveData.isConnected] gesetzt. Ein Netzwerk ist verfügbar, wenn die Liste der verfügbaren
     * Netzwerke größer 0 ist.
     */
    private fun checkValidNetworks() {
        isConnected = networkList.size > 0
        postValue(networkList.size > 0)
    }

    /**
     * Prüft die Internetverfügbarkeit anhand eines Prüfservers
     * https://kodlogs.com/93804/cannot-connect-to-server-check-whether-the-network-is-available-or-use-a-proxy-server
     * @param socketFactory SocketFactory Ermöglicht die Erstellung eines Websockets
     * @return Boolean True, wenn ein Zugriff möglich ist, ansonsten False
     */
    private fun isInternet(socketFactory: SocketFactory): Boolean {
        try {
            socketFactory
                .createSocket()
                .connect(InetSocketAddress("8.8.8.8", 53), 1500)
            return true
        } catch (e: IOException) {
            return false
        }
    }

    //endregion
}