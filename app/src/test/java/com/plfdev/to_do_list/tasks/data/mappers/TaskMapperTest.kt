package com.plfdev.to_do_list.tasks.data.mappers

import com.plfdev.to_do_list.tasks.data.entity.TaskEntity
import com.plfdev.to_do_list.tasks.domain.model.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class TaskMapperTest {

    private lateinit var taskEntityMock: TaskEntity
    private lateinit var taskMock: Task

    @Before
    fun setup() {
        taskEntityMock = mockk<TaskEntity>()
        taskMock = mockk<Task>()
    }

    @Test
    fun `toTask should correctly map TaskEntity to Task using MockK`() {
        // Arrange
        every { taskEntityMock.id } returns 1L
        every { taskEntityMock.title } returns "Test Task"
        every { taskEntityMock.description } returns "This is a test task"
        every { taskEntityMock.isCompleted } returns true
        every { taskEntityMock.isAdded } returns false
        every { taskEntityMock.isUpdated } returns true
        every { taskEntityMock.isDeleted } returns false
        every { taskEntityMock.isSynced } returns true

        // Act
        val task = taskEntityMock.toTask()

        // Assert
        assertEquals(1L, task.id)
        assertEquals("Test Task", task.title)
        assertEquals("This is a test task", task.description)
        assertEquals(true, task.isCompleted)
        assertEquals(false, task.isAdded)
        assertEquals(true, task.isUpdated)
        assertEquals(false, task.isDeleted)
        assertEquals(true, task.isSynced)

        // Verify
        verify(exactly = 1) { taskEntityMock.id }
        verify(exactly = 1) { taskEntityMock.title }
        verify(exactly = 1) { taskEntityMock.description }
    }

    @Test
    fun `toEntity should correctly map Task to TaskEntity using MockK`() {
        // Arrange
        every { taskMock.id } returns 1L
        every { taskMock.title } returns "Test Task"
        every { taskMock.description } returns "This is a test task"
        every { taskMock.isCompleted } returns true
        every { taskMock.isAdded } returns false
        every { taskMock.isUpdated } returns true
        every { taskMock.isDeleted } returns false
        every { taskMock.isSynced } returns true

        // Act
        val taskEntity = taskMock.toEntity()

        // Assert
        assertEquals(1L, taskEntity.id)
        assertEquals("Test Task", taskEntity.title)
        assertEquals("This is a test task", taskEntity.description)
        assertEquals(true, taskEntity.isCompleted)
        assertEquals(false, taskEntity.isAdded)
        assertEquals(true, taskEntity.isUpdated)
        assertEquals(false, taskEntity.isDeleted)
        assertEquals(true, taskEntity.isSynced)

        // Verify
        verify(exactly = 1) { taskMock.id }
        verify(exactly = 1) { taskMock.title }
        verify(exactly = 1) { taskMock.description }
    }
}