package com.plfdev.to_do_list.tasks.data.mappers

import com.plfdev.to_do_list.tasks.data.dto.TaskDto
import com.plfdev.to_do_list.tasks.data.entitiy.TaskEntity
import com.plfdev.to_do_list.tasks.domain.model.Task

//TaskEntity
fun TaskEntity.toTask() = Task(
    id = id,
    title = title,
    description = description.orEmpty(),
    isCompleted = isCompleted
)

//Task
fun Task.toEntity(synced: Boolean = true) = TaskEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    isSynced = synced
)

//TaskDto
fun TaskDto.toTask() = Task(
    id = id,
    title = title,
    description = description.orEmpty(),
    isCompleted = isCompleted
)

fun Task.toDto() = TaskDto(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

fun TaskEntity.toDto() = TaskDto(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

fun TaskDto.toEntity(synced: Boolean = true) = TaskEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    isSynced = synced
)
