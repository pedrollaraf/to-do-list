package com.plfdev.to_do_list.tasks.domain

import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.data.dto.TaskDto
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SyncTaskUseCasesTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var syncTasksUseCases: SyncTasksUseCases

    @Before
    fun setup() {
        taskRepository = mockk()
        syncTasksUseCases = SyncTasksUseCases(taskRepository)
    }

    @Test
    fun `invoke should return success when all tasks are synced`() = runTest {
        //Arrange
        val tasks = listOf(
            Task(id = 1L, title = "Test Task 1", description = "Test Description 1", isCompleted = false, isSynced = true),
            Task(id = 2L, title = "Test Task 2", description = "Test Description 2", isCompleted = false, isSynced = true)
        )
        // pega todas as tarefas do banco de dados local
        coEvery { taskRepository.getTasks() } returns Either.success(tasks)
        // Act
        val result = syncTasksUseCases.invoke()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(Either.emptyResult<List<Task>>(), result)
    }

    @Test
    fun `invoke should sync tasks marked as added`() = runTest {
        //Arrange
        val task = Task(1L, "Task 1", "Description 1", isSynced = false, isAdded = true)
        val syncedTask = task.copy(isSynced = true, isAdded = false)
        val tasks = listOf(task)
        coEvery { taskRepository.getTasks() } returns Either.success(tasks)
        coEvery { taskRepository.syncTaskWhenAdd(task) } returns Either.success(TaskDto(task.id.toString(), task.title, task.description))
        coEvery { taskRepository.updateTask(syncedTask) } returns Either.success(Unit) // Configurando o comportamento para updateTask
        //Act
        val result = syncTasksUseCases.invoke()
        //Assert

        // Verificando se o método updateTask foi chamado
        coVerify(exactly = 1) { taskRepository.updateTask(syncedTask) }
        coVerify(exactly = 1) { taskRepository.syncTaskWhenAdd(task) }
        coVerify(exactly = 1) { taskRepository.getTasks() }

        assertTrue(result.isSuccess)
        assertEquals(listOf(syncedTask), result.data)
    }

    @Test
    fun `invoke should return error when syncing a task fails`() = runTest {
        // Arrange
        val task = Task(1L, "Task 1", "Description 1", isSynced = false, isAdded = true)
        val tasks = listOf(task)
        coEvery { taskRepository.getTasks() } returns Either.success(tasks)
        coEvery { taskRepository.syncTaskWhenAdd(task) } returns Either.failure(DataError.NetworkError.SERVER_ERROR())

        // Act
        val result = syncTasksUseCases.invoke()

        // Assert
        coVerify(exactly = 1) { taskRepository.syncTaskWhenAdd(task) }
        assertTrue(!result.isSuccess)
        assertEquals(DataError.NetworkError.SERVER_ERROR(), result.error)
    }

    @Test
    fun `invoke should return error when updating a task locally fails`() = runTest {
        // Arrange
        val task = Task(1L, "Task 1", "Description 1", isSynced = false, isAdded = true)
        val syncedTask = task.copy(isSynced = true, isAdded = false)
        val tasks = listOf(task)
        coEvery { taskRepository.getTasks() } returns Either.success(tasks)
        coEvery { taskRepository.syncTaskWhenAdd(task) } returns Either.success(TaskDto(task.id.toString(), task.title, task.description))
        coEvery { taskRepository.updateTask(syncedTask) } returns Either.failure(DataError.LocalError.UPDATE_ERROR)

        // Act
        val result = syncTasksUseCases.invoke()

        // Assert
        coVerify(exactly = 1) { taskRepository.syncTaskWhenAdd(task) }
        coVerify(exactly = 1) { taskRepository.updateTask(syncedTask) }
        assertTrue(!result.isSuccess)
        assertEquals(DataError.LocalError.UPDATE_ERROR, result.error)
    }
}