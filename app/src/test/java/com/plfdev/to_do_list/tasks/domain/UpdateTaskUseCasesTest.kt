package com.plfdev.to_do_list.tasks.domain

import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository
import com.plfdev.to_do_list.tasks.domain.usecases.UpdateTaskUseCases
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateTaskUseCasesTest {
    private val repository: TaskRepository = mockk()
    private val useCase = UpdateTaskUseCases(repository)

    @Test
    fun `should return success when repository updates task successfully`() = runBlocking {
        val task = Task(id = 1, title = "Updated Task")
        coEvery { repository.updateTask(task) } returns Either.success(Unit)

        val result = useCase(task)

        assertEquals(Either.success(Unit), result)
    }

    @Test
    fun `should return error when repository fails to update task`() = runBlocking {
        val task = Task(id = 1, title = "Updated Task")
        coEvery { repository.updateTask(task) } returns Either.error(DataError.Local.DISK_FULL)

        val result = useCase(task)

        assert(!result.isSuccess)
    }
}
