package com.plfdev.to_do_list.tasks.domain.usecases

import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository

class UpdateTaskUseCases(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Either<Unit> {
        return taskRepository.updateTask(task)
    }
}