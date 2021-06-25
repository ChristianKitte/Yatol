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

/*
Inspired by:
https://www.youtube.com/watch?v=To9aHYD5OVk
https://github.com/AlexSheva-mason/Rick-Morty-Database/blob/master/app/src/main/java/com/shevaalex/
android/rickmortydatabase/utils/networking/ConnectionLiveData.kt
*/

// Weiter überarbeiten

/**
 *
 * @property networkCallback NetworkCallback
 * @property connectivityManeger ConnectivityManager
 * @property networkList MutableSet<Network>
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

    /**
     *
     */
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    /**
     *
     */
    private val connectivityManeger =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     *
     */
    private val networkList: MutableSet<Network> = HashSet()

    /**
     *
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
     *
     */
    override fun onInactive() {
        connectivityManeger.unregisterNetworkCallback(networkCallback)
    }

    /**
     *
     * @return <no name provided>
     */
    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        // https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onAvailable(android.net.Network)
        /**
         *
         * @param network Network
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

        // https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onLost(android.net.Network)
        /**
         *
         * @param network Network
         */
        override fun onLost(network: Network) {
            networkList.remove(network)
            checkValidNetworks()
        }

    }

    /**
     *
     */
    private fun checkValidNetworks() {
        isConnected = networkList.size > 0
        postValue(networkList.size > 0)
    }

    // https://kodlogs.com/93804/cannot-connect-to-server-check-whether-the-network-is-available-or-use-a-proxy-server
    /**
     *
     * @param socketFactory SocketFactory
     * @return Boolean
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

}