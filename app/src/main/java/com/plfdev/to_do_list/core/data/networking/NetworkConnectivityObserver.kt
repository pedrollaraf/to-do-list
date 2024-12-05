package com.plfdev.to_do_list.core.data.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent


class NetworkConnectivityObserver(context: Context) : LifecycleObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d("BRATISLAV", "onAvailable")
            _isConnected.postValue(true)

        }

        override fun onLost(network: Network) {
            Log.d("BRATISLAV", "onLost")
            _isConnected.postValue(false)

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
