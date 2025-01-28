package com.plfdev.to_do_list.tasks.presenter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.plfdev.to_do_list.core.data.networking.NetworkConnectivityObserver
import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.usecases.AddTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.GetTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.UpdateTaskUseCases
import com.plfdev.to_do_list.tasks.presenter.viewmodel.TaskViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getTaskUseCases: GetTaskUseCases
    private lateinit var addTaskUseCases: AddTaskUseCases
    private lateinit var updateTaskUseCases: UpdateTaskUseCases
    private lateinit var syncTasksUseCases: SyncTasksUseCases
    private lateinit var workManager: WorkManager
    private lateinit var networkObserver: NetworkConnectivityObserver
    private lateinit var viewModel: TaskViewModel
    private val mockNetworkState = MutableStateFlow(false)

    @Before
    fun setup() {
        // Define o Dispatcher para testes
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this) // Inicia as anotações MockK
        getTaskUseCases = mockk(relaxed = true)
        addTaskUseCases = mockk(relaxed = true)
        updateTaskUseCases = mockk(relaxed = true)
        syncTasksUseCases =  mockk(relaxed = true)
        workManager =  mockk(relaxed = true)
        networkObserver = mockk(relaxed = true)

        coEvery { networkObserver.isConnected } returns mockNetworkState

        viewModel = TaskViewModel(
            getTaskUseCases = getTaskUseCases,
            addTaskUseCases = addTaskUseCases,
            updateTaskUseCases = updateTaskUseCases,
            syncTasksUseCases = syncTasksUseCases,
            workManager = workManager,
            networkObserver = networkObserver
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `should load data successfully`() = runTest {
        val tasks = listOf(
            Task(id = 1, title = "Task 1", isCompleted = false),
            Task(id = 2, title = "Task 2", isCompleted = true)
        )
        // Mockando o retorno do UseCase
        coEvery { getTaskUseCases.invoke() } returns Either.success(tasks)

        // Emite um valor no mockNetworkState para simular a conexão de rede
        mockNetworkState.value = true

        // Chamando a função da ViewModel
        viewModel.loadTasks()
        advanceUntilIdle()

        // Verificando o resultado
        assertEquals(tasks.size, viewModel.tasks.value.size)
    }

    @Test
    fun `addTask should add a new task to the list`() = runTest {
        // Arrange
        val newTask = Task(id = null, title = "New Task", isCompleted = false)
        val taskId = 1L // ID gerado após a adição
        coEvery { addTaskUseCases.invoke(newTask) } returns Either.success(taskId)

        // Act
        viewModel.addTask(newTask)
        advanceUntilIdle()

        // Assert
        val addedTask = newTask.copy(id = taskId)
        assertEquals(addedTask, viewModel.tasks.value.last())
    }

    @Test
    fun `updateTask should update an existing task in the list`() = runTest {
        // Arrange
        val initialTasks = listOf(
            Task(id = 1, title = "Task 1", isCompleted = false),
            Task(id = 2, title = "Task 2", isCompleted = true)
        )
        coEvery { getTaskUseCases.invoke() } returns Either.success(initialTasks)
        viewModel.loadTasks()
        advanceUntilIdle()

        val updatedTask = Task(id = 1, title = "Updated Task 1", isCompleted = true)
        coEvery { updateTaskUseCases.invoke(updatedTask) } returns Either.success(Unit)

        // Act
        viewModel.updateTask(updatedTask)
        advanceUntilIdle()

        // Assert
        assertEquals(updatedTask, viewModel.tasks.value.first { it.id == 1L })
    }

    @Test
    fun `syncTasks should update the task list after sync`() = runTest {
        // Arrange
        val syncedTasks = listOf(
            Task(id = 1, title = "Synced Task 1", isCompleted = false),
            Task(id = 2, title = "Synced Task 2", isCompleted = true)
        )
        coEvery { syncTasksUseCases.invoke() } returns Either.success(syncedTasks)

        // Act
        viewModel.syncTasks()
        advanceUntilIdle()

        // Assert
        assertEquals(syncedTasks, viewModel.tasks.value)
    }

    @Test
    fun `observeNetwork should trigger sync when network is connected`() = runTest {
        // Arrange
        val syncedTasks = listOf(
            Task(id = 1, title = "Synced Task 1", isCompleted = false),
            Task(id = 2, title = "Synced Task 2", isCompleted = true)
        )
        coEvery { syncTasksUseCases.invoke() } returns Either.success(syncedTasks)
        viewModel.syncTasks()
        advanceUntilIdle()

        // Act: Simula a rede desconectada
        mockNetworkState.value = true
        advanceUntilIdle()

        // Assert: Verifica se a lista de tarefas foi atualizada
        assertEquals(syncedTasks, viewModel.tasks.value)
    }

    @Test
    fun `observeNetwork should not trigger sync when network is disconnected`() = runTest {
        // Arrange
        val initialTasks = listOf(
            Task(id = 1, title = "Task 1", isCompleted = false),
            Task(id = 2, title = "Task 2", isCompleted = true)
        )
        coEvery { getTaskUseCases.invoke() } returns Either.success(initialTasks)
        viewModel.loadTasks()
        advanceUntilIdle()

        // Act: Simula a rede desconectada
        mockNetworkState.value = false
        advanceUntilIdle()

        // Assert: Verifica se a lista de tarefas não foi alterada
        assertEquals(initialTasks, viewModel.tasks.value)
    }

    @Test
    fun `observeWorkStatus should update tasks when sync work succeeds`() = runTest {
        // Arrange
        val workId = UUID.randomUUID()
        val workInfo = mockk<WorkInfo>()
        coEvery { workInfo.state } returns WorkInfo.State.SUCCEEDED
        coEvery { workManager.getWorkInfoByIdFlow(workId) } returns flowOf(workInfo)

        val syncedTasks = listOf(
            Task(id = 1, title = "Synced Task 1", isCompleted = false),
            Task(id = 2, title = "Synced Task 2", isCompleted = true)
        )
        coEvery { getTaskUseCases.invoke() } returns Either.success(syncedTasks)

        // Act
        viewModel.observeWorkStatus(workId)
        advanceUntilIdle()

        // Assert
        assertEquals(syncedTasks, viewModel.tasks.value)
    }

    @Test
    fun `observeWorkStatus should not update tasks when sync work fails`() = runTest {
        // Arrange
        val workId = UUID.randomUUID()
        val workInfo = mockk<WorkInfo>()
        coEvery { workInfo.state } returns WorkInfo.State.FAILED
        coEvery { workManager.getWorkInfoByIdFlow(workId) } returns flowOf(workInfo)

        val initialTasks = listOf(
            Task(id = 1, title = "Task 1", isCompleted = false),
            Task(id = 2, title = "Task 2", isCompleted = true)
        )
        coEvery { getTaskUseCases.invoke() } returns Either.success(initialTasks)
        viewModel.loadTasks()
        advanceUntilIdle()

        // Act
        viewModel.observeWorkStatus(workId)
        advanceUntilIdle()

        // Assert
        assertEquals(initialTasks, viewModel.tasks.value)
    }

    @Test
    fun `loadTasks should handle failure when loading tasks`() = runTest {
        // Arrange
        coEvery { getTaskUseCases.invoke() } returns Either.failure(DataError.LocalError.GET_TASKS_ERROR)

        // Act
        viewModel.loadTasks()
        advanceUntilIdle()

        // Assert
        assert(viewModel.tasks.value.isEmpty())
    }

    @Test
    fun `addTask should handle failure when adding a task`() = runTest {
        // Arrange
        val newTask = Task(id = null, title = "New Task", isCompleted = false)
        val errorMessage = "Failed to add task"
        coEvery { addTaskUseCases.invoke(newTask) } returns Either.failure(DataError.LocalError.DISK_FULL)

        // Act
        viewModel.addTask(newTask)
        advanceUntilIdle()

        // Assert
        assert(viewModel.tasks.value.isEmpty())
    }

    @Test
    fun `updateTask should handle failure when updating a task`() = runTest {
        // Arrange
        val initialTasks = listOf(
            Task(id = 1, title = "Task 1", isCompleted = false),
            Task(id = 2, title = "Task 2", isCompleted = true)
        )
        coEvery { getTaskUseCases.invoke() } returns Either.success(initialTasks)
        viewModel.loadTasks()
        advanceUntilIdle()

        val updatedTask = Task(id = 1, title = "Updated Task 1", isCompleted = true)
        val errorMessage = "Failed to update task"
        coEvery { updateTaskUseCases.invoke(updatedTask) } returns Either.failure(DataError.LocalError.UPDATE_ERROR)

        // Act
        viewModel.updateTask(updatedTask)
        advanceUntilIdle()

        // Assert
        assertEquals(initialTasks, viewModel.tasks.value)
    }
}