package com.plfdev.to_do_list.tasks.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.plfdev.to_do_list.core.data.networking.NetworkConnectivityObserver
import com.plfdev.to_do_list.core.domain.util.Either.Companion.onFailure
import com.plfdev.to_do_list.core.domain.util.Either.Companion.onSuccess
import com.plfdev.to_do_list.core.worker.SyncWorker
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.usecases.AddTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.GetTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.UpdateTaskUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TaskViewModel (
    private val getTaskUseCases: GetTaskUseCases,
    private val addTaskUseCases: AddTaskUseCases,
    private val updateTaskUseCases: UpdateTaskUseCases,
    private val syncTasksUseCases: SyncTasksUseCases,
    private val workManager: WorkManager,
    private val networkObserver: NetworkConnectivityObserver
): ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private var lastSyncTime: Long = 0L
    private val syncCooldown: Long = 5000L // 5 segundos

    init {
        loadTasks()
        observeNetwork()
    }

    // Método para observar o estado de um WorkRequest específico
    private fun observeWorkStatus(workId: UUID) {
        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(workId).collect { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        Log.d("SyncWorker", "SUCCEEDED")
                        loadTasks()
                    } else {
                        Log.e("SyncWorker", "FAILED")
                    }
                }
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkObserver.isConnected.collect { connected ->
                if (connected) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastSyncTime >= syncCooldown) {
                        lastSyncTime = currentTime

                        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
                        workManager.enqueue(syncWorkRequest)

                        observeWorkStatus(syncWorkRequest.id)
                    }
                }
            }
        }
    }


    fun loadTasks() {
        viewModelScope.launch {
            val result = getTaskUseCases.invoke()
            result.onSuccess { tasks ->
                val mutableList = tasks.toMutableList()
                Log.d("BRATISLAV:", mutableList.toString())
                _tasks.value = mutableList
            }.onFailure {
                Log.e("DATAERROR: ", it.toString())
            }
        }
    }

    fun addTask(newTask: Task) {
        viewModelScope.launch {
            val result = addTaskUseCases.invoke(newTask)
            result.onSuccess {
                val mutableList = _tasks.value.toMutableList()
                mutableList.add(newTask.copy(id = it))
                _tasks.value = mutableList
            }.onFailure {
                Log.e("DATAERROR: ", it.toString())
            }
        }
    }

    fun updateTask(task : Task) {
        viewModelScope.launch {
            val result = updateTaskUseCases.invoke(task)
            result.onSuccess {
                val position = getPositionOfFirstId(task.id!!)
                val mutableList = _tasks.value.toMutableList()
                mutableList[position] = task
                _tasks.value = mutableList
            }.onFailure {
                Log.e("DATAERROR: ", it.toString())
            }
        }
    }

    fun syncTasks() {
        viewModelScope.launch {
            val result = syncTasksUseCases.invoke()
            result.onSuccess {
                _tasks.value = it
            }.onFailure {
                Log.e("DATAERROR: ", it.toString())
            }
        }
    }

    private fun getPositionOfFirstId(taskId: Long): Int {
        return tasks.value.indexOfFirst { it.id == taskId }
    }
}