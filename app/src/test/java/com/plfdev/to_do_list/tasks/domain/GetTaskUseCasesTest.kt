package com.plfdev.to_do_list.tasks.domain

import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository
import com.plfdev.to_do_list.tasks.domain.usecases.GetTaskUseCases
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetTaskUseCasesTest {
    private lateinit var useCase: GetTaskUseCases
    private val repository: TaskRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetTaskUseCases(repository)
    }

    @Test
    fun `invoke should return tasks from repository`() = runBlocking {
        val tasks = listOf(Task(id = 1, title = "Task"))
        coEvery { repository.getTasks() } returns Either.success(tasks)

        val result = useCase()

        assertEquals(Either.success(tasks), result)
        coVerify { repository.getTasks() }
    }
}
