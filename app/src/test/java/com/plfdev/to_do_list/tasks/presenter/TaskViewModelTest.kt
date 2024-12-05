package com.plfdev.to_do_list.tasks.presenter

import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.plfdev.to_do_list.core.data.networking.NetworkConnectivityObserver
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.usecases.AddTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.GetTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.UpdateTaskUseCases
import com.plfdev.to_do_list.tasks.presenter.viewmodel.TaskViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {
    private lateinit var viewModel: TaskViewModel
    private val getTaskUseCases: GetTaskUseCases = mockk()
    private val addTaskUseCases: AddTaskUseCases = mockk()
    private val updateTaskUseCases: UpdateTaskUseCases = mockk()
    private val syncTasksUseCases: SyncTasksUseCases = mockk()
    private val workManager: WorkManager = mockk()
    private val networkObserver: NetworkConnectivityObserver = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mock do WorkManager com um WorkInfo válido
        val workInfo = WorkInfo(
            id = UUID.randomUUID(),
            state = WorkInfo.State.SUCCEEDED,
            outputData = androidx.work.Data.EMPTY,
            tags = setOf("sync"), // Alterado para Set<String>
            runAttemptCount = 0
        )
        every { workManager.getWorkInfoByIdFlow(any()) } returns flowOf(workInfo)

        // Mock do observador de conectividade
        val networkFlow = MutableStateFlow(false) // Estado inicial desconectado
        every { networkObserver.isConnected } returns networkFlow

        viewModel = TaskViewModel(
            getTaskUseCases,
            addTaskUseCases,
            updateTaskUseCases,
            syncTasksUseCases,
            workManager,
            networkObserver
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTasks should update tasks when use case succeeds`() = runTest {
        val tasks = listOf(Task(id = 1, title = "Test Task"))
        coEvery { getTaskUseCases.invoke() } returns Either.success(tasks)

        viewModel.syncTasks() // Ou chame o método que dispara o fluxo.

        val result = viewModel.tasks.value
        assertEquals(tasks, result)
    }

    @Test
    fun `addTask should add a task and update the tasks list`() = runTest {
        val newTask = Task(id = null, title = "New Task")
        val createdTaskId = 1L
        coEvery { addTaskUseCases.invoke(newTask) } returns Either.success(createdTaskId)

        viewModel.addTask(newTask)

        val expectedTasks = listOf(newTask.copy(id = createdTaskId))
        assertEquals(expectedTasks, viewModel.tasks.value)

        coVerify { addTaskUseCases.invoke(newTask) }
    }

    @Test
    fun `updateTask should update an existing task in the tasks list`() = runTest {
        val existingTask = Task(id = 1, title = "Old Task")
        val updatedTask = existingTask.copy(title = "Updated Task")
        coEvery { updateTaskUseCases.invoke(updatedTask) } returns Either.success(Unit)

        viewModel.updateTask(updatedTask)

        val updatedTasks = viewModel.tasks.value
        assertEquals(listOf(updatedTask), updatedTasks)

        coVerify { updateTaskUseCases.invoke(updatedTask) }
    }

    @Test
    fun `syncTasks should synchronize tasks and update the list`() = runTest {
        val syncedTasks = listOf(Task(id = 1, title = "Synced Task", isSynced = true))
        coEvery { syncTasksUseCases.invoke() } returns Either.success(syncedTasks)

        viewModel.syncTasks()

        val currentTasks = viewModel.tasks.value
        assertEquals(syncedTasks, currentTasks)

        coVerify { syncTasksUseCases.invoke() }
    }

    @Test
    fun `should trigger sync when network becomes available`() = runTest {
        val networkFlow = MutableStateFlow(false) // Estado inicial
        every { networkObserver.isConnected } returns networkFlow

        coEvery { syncTasksUseCases.invoke() } returns Either.success(emptyList())

        // Simula que a rede ficou disponível
        networkFlow.value = true

        coVerify { syncTasksUseCases.invoke() }
    }
}
