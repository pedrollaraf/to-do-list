package com.plfdev.to_do_list.tasks.domain.usecases

import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository

class GetTaskUseCases(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(): Either<List<Task>> {
        return taskRepository.getTasks()
    }
}