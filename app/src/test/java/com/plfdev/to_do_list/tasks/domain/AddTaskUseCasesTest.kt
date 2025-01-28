package com.plfdev.to_do_list.tasks.domain

import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository
import com.plfdev.to_do_list.tasks.domain.usecases.AddTaskUseCases
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddTaskUseCasesTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var addTaskUseCases: AddTaskUseCases

    @Before
    fun setup() {
        taskRepository = mockk()
        addTaskUseCases = AddTaskUseCases(taskRepository)
    }

    @Test
    fun `invoke should return Either with ID when task is added successfully`() = runTest {
        // Arrange
        val task = Task(id = 0, title = "Test Task", description = "Test Description", isCompleted = false)
        coEvery { taskRepository.addTask(task) } returns Either.success(1L)

        // Act
        val result = addTaskUseCases.invoke(task)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1L, result.data)
    }

    @Test
    fun `invoke should return Either with error when repository fails`() = runTest {
        // Arrange
        val task = Task(id = 0, title = "Test Task", description = "Test Description", isCompleted = false)

        coEvery { taskRepository.addTask(task) } returns Either.failure(DataError.LocalError.DISK_FULL)

        // Act
        val result = addTaskUseCases.invoke(task)

        // Assert
        assertTrue(!result.isSuccess)
        val error = result.error as DataError.LocalError.DISK_FULL
        assertEquals(DataError.LocalError.DISK_FULL, error)
        assertEquals("O armazenamento do dispositivo está cheio.", error.message)
    }
}