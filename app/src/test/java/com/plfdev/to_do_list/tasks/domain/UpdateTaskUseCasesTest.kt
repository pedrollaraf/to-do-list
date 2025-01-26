package com.plfdev.to_do_list.tasks.domain

import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository
import com.plfdev.to_do_list.tasks.domain.usecases.UpdateTaskUseCases
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateTaskUseCasesTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var updateTaskUseCases: UpdateTaskUseCases

    @Before
    fun setup() {
        taskRepository = mockk()
        updateTaskUseCases = UpdateTaskUseCases(taskRepository)
    }

    @Test
    fun `invoke should return success when task is updated successfully`() = runTest {
        // Arrange
        val task = Task(1L, "Task 1", "Description 1", true)
        coEvery { taskRepository.updateTask(task) } returns Either.success(Unit)

        // Act
        val result = updateTaskUseCases.invoke(task)

        // Assert
        coVerify(exactly = 1) { taskRepository.updateTask(task) }
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.data)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Arrange
        val task = Task(1, "Task 1", "Description 1", true)
        coEvery { taskRepository.updateTask(task) } returns Either.error(DataError.LocalError.UPDATE_ERROR)

        // Act
        val result = updateTaskUseCases.invoke(task)

        // Assert
        assertTrue(!result.isSuccess)
        val error = result.error as DataError.LocalError.UPDATE_ERROR
        assertEquals(DataError.LocalError.UPDATE_ERROR, result.error)
        assertEquals("Erro ao atualizar os dados.", error.message)
    }
}