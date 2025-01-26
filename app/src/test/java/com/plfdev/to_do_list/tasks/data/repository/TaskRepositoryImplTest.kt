package com.plfdev.to_do_list.tasks.data.repository

import com.plfdev.to_do_list.core.commons.FileReaderHelper
import com.plfdev.to_do_list.core.data.networking.HttpClientFactory
import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.data.dao.TaskDao
import com.plfdev.to_do_list.tasks.data.mappers.toEntity
import com.plfdev.to_do_list.tasks.data.mappers.toTask
import com.plfdev.to_do_list.tasks.domain.model.Task
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.HttpHeaders
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class TaskRepositoryImplTest {

    private lateinit var taskDao: TaskDao
    private lateinit var httpClient: HttpClient
    private lateinit var mockWebServer: MockWebServer
    private lateinit var mockResponse: MockResponse
    private lateinit var taskRepository: TaskRepositoryImpl


    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        mockResponse = MockResponse().addHeader(HttpHeaders.ContentType, "application/json")
        httpClient = HttpClientFactory.create(engine = CIO.create())
        taskDao = mockk<TaskDao>()
        taskRepository = TaskRepositoryImpl(
            httpClient = httpClient,
            taskDao = taskDao,
            baseUrl = mockWebServer.url("/").toString()
        )
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    //region GetTasks
    @Test
    fun `should return success when try to get tasks`() = runBlocking {
        // Dado que o TaskDao retorna uma lista de tarefas simuladas como TaskEntity
        val taskEntities = listOf(Task(id = 1, title = "Test Task", description = "Test Task")).map { it.toEntity() }
        coEvery { taskDao.getTasks() } returns taskEntities

        // Quando chamamos getTasks
        val result = taskRepository.getTasks()

        // Então devemos receber o sucesso com a lista convertida para Task
        val expectedTasks = taskEntities.map { it.toTask() }
        assertEquals(result, Either.success(expectedTasks))
    }

    @Test
    fun `should return error when try to get tasks`() = runTest {
        // Dado que o TaskDao lança uma exceção ao tentar obter as tarefas
        coEvery { taskDao.getTasks() } throws Exception("Database error")

        // Quando chamamos getTasks
        val result = taskRepository.getTasks()

        // Então devemos receber um erro com o tipo correto de erro
        val expectedError = Either.error<List<Task>>(DataError.LocalError.GET_TASKS_ERROR)
        assertEquals(result, expectedError)
    }
    //endregion

    //region AddTask
    @Test
    fun `should return success when try to to add task to DAO`() = runTest {
        val task = Task(1, "New Task", "Description")

        // Simula que o DAO retorna um id de inserção
        coEvery { taskDao.insertTask(any()) } returns 1L

        // Executa o método
        val result = taskRepository.addTask(task)

        // Verifica o retorno
        assertTrue(result.isSuccess)
        assertEquals(1L, result.data)

        // Alternativa para verificar explicitamente o resultado com a comparação de Either:
        val expectedResult = Either.success(1L)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `should return error when try to add task`() = runTest {
        // Dado que o DAO lança uma exceção ao tentar inserir a tarefa
        val task = Task(1, "New Task", "Description")
        coEvery { taskDao.insertTask(any()) } throws Exception("Disk full")

        // Quando chamamos addTask
        val result = taskRepository.addTask(task)

        // Então devemos receber um erro com o tipo correto de erro
        val expectedError = Either.error<DataError>(DataError.LocalError.DISK_FULL)
        assertEquals(expectedError, result)
    }
    //endregion

    //region UpdateTask
    @Test
    fun `should return success when try to update task to DAO` () = runTest {
        val task = Task(1, "Task Updated", "Description Updated")

        // Simula que o DAO será atualizado
        coEvery { taskDao.updateTask(any()) } returns Unit

        // Executa o método
        val result = taskRepository.updateTask(task)

        // Verifica o retorno
        assertTrue(result.isSuccess)
        assertEquals(Unit, result.data)

        // Alternativa para verificar explicitamente o resultado com a comparação de Either:
        val expectedResult = Either.success(Unit)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `should return failure when try to update task to DAO`() = runTest {
        // Dado que o DAO lança uma exceção ao tentar inserir a tarefa
        val task = Task(1, "Task Updated", "Description Updated")
        coEvery { taskDao.updateTask(any()) } throws Exception("Update Error")

        // Quando chamamos addTask
        val result = taskRepository.updateTask(task)

        // Então devemos receber um erro com o tipo correto de erro
        val expectedError = Either.error<DataError>(DataError.LocalError.UPDATE_ERROR)
        assertEquals(expectedError, result)
    }
    //endregion

    //region syncTaskWhenAdd
    @Test
    fun `syncTaskWhenAdd should return success when API call is successful`() = runTest {
        // Configurar resposta simulada
        val resource = FileReaderHelper.readFileFromResources("sync_task_when_add.json")
        mockResponse.setResponseCode(200).setBody(resource)
        mockWebServer.enqueue(mockResponse)

        // Realizar o teste
        val task = Task(id = 1, title = "Test Task")
        val result = taskRepository.syncTaskWhenAdd(task)

        // Verificar resultado
        assertTrue(result.isSuccess)
        assertEquals("1", result.data?.id)

        // Verificar requisição
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/tasks", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)
    }

    @Test
    fun `syncTaskWhenAdd should return error when API call fails`() = runTest {
        // Configurar MockWebServer para retornar erro
        val mockResponseFile = FileReaderHelper.readFileFromResources("server_error.json").trimIndent()
        mockResponse.setResponseCode(500).setBody(
            mockResponseFile
        )
        mockWebServer.enqueue(mockResponse)

        // Realizar o teste
        val task = Task(id = 1, title = "Test Task")
        val result = taskRepository.syncTaskWhenAdd(task)

        // Verificar requisição
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/tasks", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)

        // Verificar resultado
        assertTrue(!result.isSuccess)
        val error = result.error as DataError.NetworkError.SERVER_ERROR
        assertEquals(500, error.code)
        //val message = Json.parseToJsonElement(mockResponseFile).jsonObject["message"]?.jsonPrimitive?.content
        //assertEquals(message, error.message)
    }

    //endregion

    //region syncTaskWhenUpdate
    @Test
    fun `syncTaskWhenUpdate should return success when API call is successful`() = runTest {
        // Configurar resposta simulada
        val resource = FileReaderHelper.readFileFromResources("sync_task_when_update.json")
        mockResponse.setResponseCode(200).setBody(resource)
        mockWebServer.enqueue(mockResponse)

        // Realizar o teste
        val task = Task(id = 1, title = "Test Task Updated")
        val result = taskRepository.syncTaskWhenUpdate(task)

        // Verificar resultado
        assertTrue(result.isSuccess)
        assertEquals("1", result.data?.id)
        assertEquals("Test Task Updated", result.data?.title)

        // Verificar requisição
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/tasks/${task.id}", recordedRequest.path)
        assertEquals("PUT", recordedRequest.method)
    }

    @Test
    fun `syncTaskWhenUpdate should return error when API call fails`() = runTest {
        // Configurar MockWebServer para retornar erro
        val mockResponseFile = FileReaderHelper.readFileFromResources("server_error.json").trimIndent()
        mockResponse.setResponseCode(500).setBody(
            mockResponseFile
        )
        mockWebServer.enqueue(mockResponse)

        // Realizar o teste
        val task = Task(id = 1, title = "Test Task Updated")
        val result = taskRepository.syncTaskWhenUpdate(task)

        // Verificar requisição
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/tasks/${task.id}", recordedRequest.path)
        assertEquals("PUT", recordedRequest.method)

        // Verificar resultado
        assertTrue(!result.isSuccess)
        val error = result.error as DataError.NetworkError.SERVER_ERROR
        assertEquals(500, error.code)
        //val message = Json.parseToJsonElement(mockResponseFile).jsonObject["message"]?.jsonPrimitive?.content
        //assertEquals(message, error.message)
    }
    //endregion

}