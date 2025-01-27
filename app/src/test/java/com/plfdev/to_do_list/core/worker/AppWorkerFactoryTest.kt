package com.plfdev.to_do_list.core.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases
import io.mockk.mockk
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class AppWorkerFactoryTest {

    private lateinit var mockSyncTasksUseCases :SyncTasksUseCases
    private lateinit var workerFactory: AppWorkerFactory

    @Before
    fun setUp() {
        mockSyncTasksUseCases = mockk<SyncTasksUseCases>(relaxed = true)
        workerFactory = AppWorkerFactory(mockSyncTasksUseCases)
    }

    @Test
    fun `createWorker should return SyncWorker for valid worker class`() {
        // Arrange
        val context = mockk<Context>(relaxed = true)
        val workerParameters = mockk<WorkerParameters>(relaxed = true)

        // Act
        val worker = workerFactory.createWorker(
            context,
            SyncWorker::class.java.name,
            workerParameters
        )

        // Assert
        assertTrue(worker is SyncWorker)
    }

    @Test
    fun `createWorker should return null for unknown worker class`() {
        // Arrange
        val context = mockk<Context>(relaxed = true)
        val workerParameters = mockk<WorkerParameters>(relaxed = true)

        // Act
        val worker = workerFactory.createWorker(
            context,
            "UnknownWorker",
            workerParameters
        )

        // Assert
        assertNull(worker)
    }
}