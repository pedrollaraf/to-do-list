package com.plfdev.to_do_list.tasks.domain

import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository
import com.plfdev.to_do_list.tasks.domain.usecases.GetTaskUseCases
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTaskUseCasesTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var getTaskUseCases: GetTaskUseCases

    @Before
    fun setup() {
        taskRepository = mockk()
        getTaskUseCases = GetTaskUseCases(taskRepository)
    }

    @Test
    fun `invoke should return Either with ID when task is added successfully`() = runTest {
        // Arrange
        val tasks = listOf(
            Task(id = 1L, title = "Test Task 1", description = "Test Description 1", isCompleted = false),
            Task(id = 2L, title = "Test Task 2", description = "Test Description 2", isCompleted = false)
        )
        coEvery { taskRepository.getTasks() } returns Either.success(tasks)

        // Act
        val result = getTaskUseCases.invoke()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.data?.size)
        assertEquals(1L, result.data?.first()?.id)
    }

    @Test
    fun `invoke should return Either with error when repository fails`() = runTest {
        // Arrange
        coEvery { taskRepository.getTasks() } returns Either.failure(DataError.LocalError.GET_TASKS_ERROR)

        // Act
        val result = getTaskUseCases.invoke()

        // Assert
        assertTrue(!result.isSuccess)
        val error = result.error as DataError.LocalError.GET_TASKS_ERROR
        assertEquals(DataError.LocalError.GET_TASKS_ERROR, error)
        assertEquals("Erro ao buscar as tarefas.", error.message)
    }
}