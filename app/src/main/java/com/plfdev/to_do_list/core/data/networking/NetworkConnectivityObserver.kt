package com.plfdev.to_do_list.core.data.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class NetworkConnectivityObserver(context: Context) : LifecycleObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> get() = _isConnected.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d("BRATISLAV", "onAvailable")
            _isConnected.value = true
        }

        override fun onUnavailable() {
            Log.d("BRATISLAV", "onUnavailable")
            _isConnected.value = false
        }

        override fun onLost(network: Network) {
            Log.d("BRATISLAV", "onLost")
            _isConnected.value = false
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
