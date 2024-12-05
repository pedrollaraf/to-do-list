package com.plfdev.to_do_list.tasks.data.mappers

import com.plfdev.to_do_list.tasks.data.entitiy.TaskEntity
import com.plfdev.to_do_list.tasks.domain.model.Task

//TaskEntity
fun TaskEntity.toTask(): Task {
    return Task(
        id = this.id,
        title = this.title,
        description = this.description.orEmpty(),
        isCompleted = this.isCompleted,
        isAdded = this.isAdded,
        isUpdated = this.isUpdated,
        isDeleted = this.isDeleted,
        isSynced = this.isSynced
    )
}


//Task
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        isCompleted = this.isCompleted,
        isAdded = this.isAdded,
        isUpdated = this.isUpdated,
        isDeleted = this.isDeleted,
        isSynced = this.isSynced
    )
}


//TaskDto
//fun TaskDto.toTask(
//    isSynced: Boolean = true
//) = Task(
//    id = id,
//    title = title,
//    description = description.orEmpty(),
//    isCompleted = isCompleted,
//    isSynced = isSynced,
//)

