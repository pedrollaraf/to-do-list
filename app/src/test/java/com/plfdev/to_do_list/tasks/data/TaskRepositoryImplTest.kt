package com.plfdev.to_do_list.tasks.data

import com.plfdev.to_do_list.core.data.networking.constructUrl
import com.plfdev.to_do_list.tasks.data.dao.TaskDao
import com.plfdev.to_do_list.tasks.data.dto.TaskDto
import com.plfdev.to_do_list.tasks.data.repository.TaskRepositoryImpl
import com.plfdev.to_do_list.tasks.domain.model.Task
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import io.mockk.*

class TaskRepositoryImplTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var httpClient: HttpClient
    private lateinit var repository: TaskRepositoryImpl
    private val taskDao: TaskDao = mockk()

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        httpClient = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }

        repository = TaskRepositoryImpl(taskDao, httpClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `syncTaskWhenAdd should return TaskDto on success`() = runBlocking {
        val task = Task(id = null, title = "New Task", isSynced = false)
        val taskDto = TaskDto(id = "1", title = "New Task DTO")

        // Configurando a resposta simulada do MockWebServer
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": 1,
                    "title": "New Task DTO"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        mockkStatic("com.plfdev.to_do_list.core.data.networking.UrlUtilsKt")
        every { constructUrl(any()) } returns mockWebServer.url("/tasks").toString()

        val response = repository.syncTaskWhenAdd(task)

        assert(response.isSuccess)
        assertEquals(taskDto, response.data)

        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/tasks", request.path)
        assertEquals("""{"id":null,"title":"New Task","isSynced":false}""", request.body.readUtf8())
    }

    @Test
    fun `syncTaskWhenUpdate should return TaskDto on success`() = runBlocking {
        val task = Task(id = 1, title = "Updated Task", isSynced = false)
        val taskDto = TaskDto(id = "1", title = "Updated Task DTO")

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": 1,
                    "title": "Updated Task DTO"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // Mock para o método constructUrl
        mockkStatic("com.plfdev.to_do_list.core.data.networking.UrlUtilsKt")
        every { constructUrl(any()) } returns mockWebServer.url("/tasks/1").toString()

        val response = repository.syncTaskWhenUpdate(task)

        assert(response.isSuccess)
        assertEquals(taskDto, response.data)

        val request = mockWebServer.takeRequest()
        assertEquals("PUT", request.method)
        assertEquals("/tasks/1", request.path)
        assertEquals("""{"id":1,"title":"Updated Task","isSynced":false}""", request.body.readUtf8())
    }
}
