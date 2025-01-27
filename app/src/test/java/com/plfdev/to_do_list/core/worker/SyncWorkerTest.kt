package com.plfdev.to_do_list.core.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SyncWorkerTest {

    private lateinit var syncWorker: SyncWorker
    private lateinit var mockContext: Context
    private lateinit var mockWorkerParams : WorkerParameters
    private lateinit var mockSyncTasksUseCases : SyncTasksUseCases

    @Before
    fun setUp() {
        mockContext = mockk<Context>(relaxed = true)
        mockWorkerParams = mockk<WorkerParameters>(relaxed = true)
        mockSyncTasksUseCases = mockk<SyncTasksUseCases>()
        syncWorker = SyncWorker(mockContext, mockWorkerParams, mockSyncTasksUseCases)
    }

    @Test
    fun `doWork should return success when use case succeeds`() = runTest {
        // Arrange
        val tasks = listOf(
            Task(id = 1L, title = "Test Task 1", description = "Test Description 1", isCompleted = false),
            Task(id = 2L, title = "Test Task 2", description = "Test Description 2", isCompleted = false)
        )
        coEvery { mockSyncTasksUseCases.invoke() } returns Either.success(tasks)

        // Act
        val result = syncWorker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { mockSyncTasksUseCases.invoke() }
    }

    @Test
    fun `doWork should return failure when use case fails`() = runTest {
        // Arrange
        coEvery { mockSyncTasksUseCases.invoke() } returns Either.error(DataError.LocalError.UPDATE_ERROR)

        // Act
        val result = syncWorker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.failure(), result)
        coVerify { mockSyncTasksUseCases.invoke() }
    }

    @Test
    fun `doWork should return failure when an exception is thrown`() = runTest {
        // Arrange
        coEvery { mockSyncTasksUseCases.invoke() } throws Exception("Unexpected error")

        // Act
        val result = syncWorker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.failure(), result)
        coVerify { mockSyncTasksUseCases.invoke() }
    }
}