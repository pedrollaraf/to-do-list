package com.plfdev.to_do_list.tasks.domain.repository

import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.data.dto.TaskDto
import com.plfdev.to_do_list.tasks.domain.model.Task

interface TaskRepository {
    suspend fun getTasks(): Either<List<Task>>
    suspend fun addTask(task: Task): Either<Long>
    suspend fun updateTask(task: Task): Either<Unit>
    suspend fun syncTaskWhenAdd(task: Task): Either<TaskDto>
    suspend fun syncTaskWhenUpdate(task: Task): Either<TaskDto>
}