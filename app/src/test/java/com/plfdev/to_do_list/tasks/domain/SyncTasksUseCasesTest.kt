package com.plfdev.to_do_list.tasks.domain

import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.data.dto.TaskDto
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncTasksUseCasesTest {
    private val repository: TaskRepository = mockk()
    private val useCase = SyncTasksUseCases(repository)

    @Test
    fun `should sync all tasks successfully`() = runBlocking {
        val tasks = listOf(
            Task(id = 1, title = "Task 1", isSynced = false, isAdded = true),
            Task(id = 2, title = "Task 2", isSynced = true)
        )
        val syncedTaskDto = TaskDto(id = "1", title = "Task 1 Synced")
        coEvery { repository.getTasks() } returns Either.success(tasks)
        coEvery { repository.syncTaskWhenAdd(tasks[0]) } returns Either.success(syncedTaskDto)
        coEvery { repository.updateTask(any()) } returns Either.success(Unit)

        val result = useCase()

        assert(result.isSuccess)
        assertEquals(1, result.data?.count { it.isSynced })
    }

    @Test
    fun `should return error when syncing fails`() = runBlocking {
        val tasks = listOf(Task(id = 1, title = "Task 1", isSynced = false))
        coEvery { repository.getTasks() } returns Either.success(tasks)
        coEvery { repository.syncTaskWhenAdd(any()) } returns Either.error(DataError.NetworkError.SERVER_ERROR)

        val result = useCase()

        assert(!result.isSuccess)
    }

    @Test
    fun `should return empty list if all tasks are already synced`() = runBlocking {
        val tasks = listOf(
            Task(id = 1, title = "Task 1", isSynced = true),
            Task(id = 2, title = "Task 2", isSynced = true)
        )
        coEvery { repository.getTasks() } returns Either.success(tasks)

        val result = useCase()

        assert(result.isSuccess)
        assertEquals(emptyList<Task>(), result.data)
    }
}
