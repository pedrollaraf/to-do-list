package com.plfdev.to_do_list.tasks.domain.usecases

import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.data.dto.TaskDto
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository

class SyncTasksUseCases(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(): Either<List<Task>> {
        val tasks = taskRepository.getTasks().data ?: emptyList()
        val newList = tasks.toMutableList()
        var result: Either<TaskDto> = Either.emptyResult()

        val hasUnSyncedTasks = tasks.any { !it.isSynced }

        if(hasUnSyncedTasks) {
            tasks.map { task ->
                var taskSync: Task = task
                if(!task.isSynced) {
                    if(task.isDeleted) {
                        if(task.isAdded) {
                            result = taskRepository.syncTaskWhenAdd(task)
                            if(result.isSuccess) taskSync = task.copy(isSynced = true, isAdded = false)
                        } else {
                            result = taskRepository.syncTaskWhenUpdate(task)
                            if(result.isSuccess) taskSync = task.copy(isSynced = true, isUpdated = false)
                        }
                    }
                    else if(task.isUpdated) {
                        if(task.isAdded) {
                            result = taskRepository.syncTaskWhenAdd(task)
                            if(result.isSuccess) taskSync = task.copy(isSynced = true, isAdded = false)
                        } else {
                            result = taskRepository.syncTaskWhenUpdate(task)
                            if(result.isSuccess) taskSync = task.copy(
                                isSynced = true,
                                isAdded = false,
                                isUpdated = false,
                                isDeleted = false,
                            )
                        }
                    }
                    else if(task.isAdded) {
                        result = taskRepository.syncTaskWhenAdd(task)
                        if(result.isSuccess) taskSync = task.copy(isSynced = true, isAdded = false)
                    }

                    if(result.isSuccess) {
                        val updatedDb = taskRepository.updateTask(taskSync)//LOCAL
                        if(updatedDb.isSuccess) {
                            val position = newList.indexOfFirst { it.id == taskSync.id }
                            newList[position] = taskSync
                        } else {
                            return Either.error(DataError.Local.UPDATE_ERROR)
                        }
                    } else{
                        return Either.error(DataError.NetworkError.SERVER_ERROR)
                    }
                }
            }
            return Either.success(newList)
        }

        return Either.emptyResult()
    }
}

//taskRepository.getTasks().onSuccess { tasks ->
//    tasks.map { task ->
//        if(!task.isSynced) {
//            //EU ADICIONO NO MOCK
//            if(task.needSyncWhenAdd) {
//
//            }
//            //EU ATUALIZO NO MOCK
//            //EU DELETO NO MOCK
//        } else {
//            //EU ATUALIZO
//            //EU DELETO
//        }
//    }
//}