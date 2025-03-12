package com.example.todosomma.ui

import com.example.todosomma.data.TodoRepository
import com.example.todosomma.data.model.Result
import com.example.todosomma.data.model.Todo
import com.example.todosomma.data.network.SyncManager
import com.example.todosomma.notifications.TodoNotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import java.util.UUID

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TodoViewModelTest {


    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var todoRepository: TodoRepository

    @Mock
    private lateinit var syncManager: SyncManager

    @Mock
    private lateinit var todoNotificationService: TodoNotificationService

    private lateinit var viewModel: TodoViewModel

    private lateinit var todo: Todo

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        `when`(todoRepository.todos).thenReturn(flowOf(emptyList()))
        runBlocking {
            `when`(todoRepository.hasPendingUploadTodos()).thenReturn(false)
        }
        viewModel = TodoViewModel(todoRepository, syncManager, todoNotificationService)

        todo = Todo(
            id = UUID.randomUUID().toString(),
            title = "Test Todo",
            description = "Test Desc",
            serverId = null,
            createdBy = "Test",
            createdDate = System.currentTimeMillis(),
            dueDate = System.currentTimeMillis(),
            uploadedDate = 0L,
            completedDate = 0L,
            isDeleted = false
        )
    }

    @Test
    fun `loadTodos should update UI state with todos`() = testDispatcher.runBlockingTest  {
        val todos = listOf(todo.copy())
        `when`(todoRepository.todos).thenReturn(flowOf(todos))
        `when`(todoRepository.hasPendingUploadTodos()).thenReturn(false)

        viewModel = TodoViewModel(todoRepository, syncManager, todoNotificationService)

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(todos, viewModel.uiState.value.todos)
    }

    @Test
    fun `createTodo should insert a todo and schedule notification`() = runBlocking {
        val todo = todo.copy()
        `when`(todoRepository.insert(any())).thenReturn(Result.Success(Unit))
        `when`(todoRepository.hasPendingUploadTodos()).thenReturn(true)

        viewModel.createTodo("New Todo", "Description", 12345L)

        verify(todoRepository).insert(any())
        verify(todoNotificationService).scheduleNotification(any())
    }

    @Test
    fun `removeTodo should delete todo and cancel notification`() = runBlocking {
        val todo = todo.copy()
        `when`(todoRepository.delete(any())).thenReturn(Result.Success(Unit))
        `when`(todoRepository.hasPendingUploadTodos()).thenReturn(false)

        viewModel.removeTodo(todo)

        verify(todoRepository).delete(any())
        verify(todoNotificationService).cancelNotification(todo.id)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}