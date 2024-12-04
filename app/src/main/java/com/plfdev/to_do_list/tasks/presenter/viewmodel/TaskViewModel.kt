package com.plfdev.to_do_list.tasks.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plfdev.to_do_list.core.domain.util.Either.Companion.onFailure
import com.plfdev.to_do_list.core.domain.util.Either.Companion.onSuccess
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.usecases.AddTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.DeleteTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.GetTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.UpdateTaskUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel (
    private val getTaskUseCases: GetTaskUseCases,
    private val addTaskUseCases: AddTaskUseCases,
    private val updateTaskUseCases: UpdateTaskUseCases,
    private val deleteTaskUseCases: DeleteTaskUseCases,
    private val syncTasksUseCases: SyncTasksUseCases,
): ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow().onStart {
        loadTasks()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        emptyList()
    )

    private fun loadTasks() {
        viewModelScope.launch {
            val result = getTaskUseCases.invoke()
            result.onSuccess { tasks ->
                val mutableList = tasks.toMutableList()
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
                mutableList.add(newTask)
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
                val position = getPositionOfFirstId(task.id)
                val mutableList = _tasks.value.toMutableList()
                mutableList[position] = task
                _tasks.value = mutableList
            }.onFailure {
                Log.e("DATAERROR: ", it.toString())
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            val result = deleteTaskUseCases.invoke(task)
            result.onSuccess {
                val mutableList = _tasks.value.toMutableList()
                mutableList.remove(task)
                _tasks.value = mutableList
            }.onFailure {
                Log.e("DATAERROR: ", it.toString())
            }
        }
    }

    fun sync() {
        viewModelScope.launch {
            val result = syncTasksUseCases.invoke()
            result.onSuccess {
                Log.e("DATA: ", "SUCESSO")
            }.onFailure {
                Log.e("DATAERROR: ", it.toString())
            }
        }
    }

    private fun getPositionOfFirstId(taskId: String): Int {
        return tasks.value.indexOfFirst { it.id == taskId }
    }
}