package com.example.todosomma.data.network


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val networkMonitor: NetworkMonitor,
) {

    fun startSyncing(scope: CoroutineScope, onSyncTriggered: () -> Unit) {
        scope.launch(Dispatchers.IO) {
            networkMonitor.isConnected.collectLatest { isConnected ->
                if (isConnected) {
                    onSyncTriggered()
                }
            }
        }
    }
}