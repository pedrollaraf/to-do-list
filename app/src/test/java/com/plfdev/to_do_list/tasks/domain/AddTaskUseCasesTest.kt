import com.plfdev.to_do_list.core.domain.util.DataError
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.tasks.domain.model.Task
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository
import com.plfdev.to_do_list.tasks.domain.usecases.AddTaskUseCases
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddTaskUseCasesTest {
    private lateinit var useCase: AddTaskUseCases
    private val repository: TaskRepository = mockk()

    @Before
    fun setUp() {
        useCase = AddTaskUseCases(repository)
    }

    @Test
    fun `should return success when repository adds task successfully`() = runBlocking {
        val task = Task(id = null, title = "New Task")
        val taskId = 1L
        coEvery { repository.addTask(task) } returns Either.success(taskId)

        val result = useCase(task)

        assertEquals(Either.success(taskId), result)
    }

    @Test
    fun `should return error when repository fails to add task`() = runBlocking {
        val task = Task(id = null, title = "New Task")
        coEvery { repository.addTask(task) } returns Either.error(DataError.Local.DISK_FULL)

        val result = useCase(task)

        assert(!result.isSuccess)
    }
}
