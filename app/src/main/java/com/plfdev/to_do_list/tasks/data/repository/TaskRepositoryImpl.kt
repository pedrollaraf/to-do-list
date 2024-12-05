package com.plfdev.to_do_list.tasks.data.repository


import android.util.Log
import com.plfdev.to_do_list.core.data.networking.constructUrl
import com.plfdev.to_do_list.core.data.networking.safeCall
import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.core.domain.util.Either.Companion.error
import com.plfdev.to_do_list.core.domain.util.Either.Companion.success
import com.plfdev.to_do_list.tasks.data.dao.TaskDao
import com.plfdev.to_do_list.tasks.data.dto.TaskDto
import com.plfdev.to_do_list.tasks.data.mappers.toEntity
import com.plfdev.to_do_list.tasks.data.mappers.toTask
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val httpClient: HttpClient
): TaskRepository {
    override suspend fun getTasks(): Either<List<Task>> {
        try {
            val tasks = taskDao.getTasks().map { task ->
                task.toTask()
            }
            return success(tasks)
        } catch (exception: Exception) {
            return error(DataError.Local.GET_TASKS_ERROR)
        }
    }

    override suspend fun addTask(task: Task): Either<Long> {
        try {
            val entity = task.toEntity()
            val result = taskDao.insertTask(entity)
            return success(result)
        } catch (exception: Exception) {
            Log.e("DATAERROR:",exception.toString())
            return error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun updateTask(task: Task): Either<Unit> {
        try {
            val entity = task.toEntity()
            val result = taskDao.updateTask(entity)
            return success(result)
        } catch (exception: Exception) {
            return error(DataError.Local.UPDATE_ERROR)
        }
    }

    override suspend fun syncTaskWhenAdd(task: Task): Either<TaskDto> {
        return safeCall<TaskDto> {
            httpClient.post(
                urlString = constructUrl("/tasks")
            ) {
                contentType(ContentType.Application.Json)
                setBody(task)
            }
        }
    }

    override suspend fun syncTaskWhenUpdate(task: Task): Either<TaskDto> {
        return safeCall<TaskDto> {
            httpClient.put(
                urlString = constructUrl("/tasks/${task.id}")
            ) {
                contentType(ContentType.Application.Json)
                setBody(task)
            }
        }
    }
}