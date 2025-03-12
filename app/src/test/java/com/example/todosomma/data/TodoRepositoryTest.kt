package com.example.todosomma.data


import android.content.Context
import com.example.todosomma.data.local.TodoLocalDataSource
import com.example.todosomma.data.model.Result
import com.example.todosomma.data.model.Todo
import com.example.todosomma.data.remote.TodoRemoteDataSource
import com.example.todosomma.util.NetworkUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.UUID
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TodoRepositoryTest {

    @Mock
    private lateinit var todoLocalDataSource: TodoLocalDataSource

    @Mock
    private lateinit var todoRemoteDataSource: TodoRemoteDataSource

    @Mock
    lateinit var networkUtils: NetworkUtils

    @Mock
    private lateinit var context: Context

    private lateinit var todoRepository: TodoRepositoryImpl

    private lateinit var todo: Todo

    @Before
    fun setup() {
        todoRepository =
            TodoRepositoryImpl(todoLocalDataSource, todoRemoteDataSource, networkUtils, context)
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
    fun `insert should store locally when offline`() = runTest {
        `when`(networkUtils.isNetworkAvailable()).thenReturn(false)

        val result = todoRepository.insert(todo)

        verify(todoLocalDataSource).insert(todo)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `insert should upload and store locally when online`() = runTest {
        `when`(networkUtils.isNetworkAvailable()).thenReturn(true)
        val todo = todo.copy()
        val serverResponse = mapOf("name" to "serverId123")

        `when`(todoRemoteDataSource.createTodo(any())).thenReturn(serverResponse)
        `when`(todoLocalDataSource.insert(any())).thenReturn(Unit)

        val result = todoRepository.insert(todo)

        verify(todoRemoteDataSource).createTodo(any())
        verify(todoLocalDataSource).insert(any())
        assertTrue(result is Result.Success)
    }

    @Test
    fun `delete should soft delete when offline`() = runTest {
        `when`(networkUtils.isNetworkAvailable()).thenReturn(false)
        val todo = todo.copy()

        val result = todoRepository.delete(todo)

        verify(todoLocalDataSource).softDelete(todo)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `delete should remove from remote when online`() = runTest {
        `when`(networkUtils.isNetworkAvailable()).thenReturn(true)
        val todo = todo.copy(serverId = "TestServerID")

        val result = todoRepository.delete(todo)

        verify(todoRemoteDataSource).deleteTodo(todo)
        verify(todoLocalDataSource).delete(todo)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `syncTodosFromRemote should fetch and store todos when online`() = runTest {
        `when`(networkUtils.isNetworkAvailable()).thenReturn(true)
        val todos = listOf(
            todo.copy(id = UUID.randomUUID().toString()),
            todo.copy(id = UUID.randomUUID().toString())
        )
        `when`(todoRemoteDataSource.getTodos()).thenReturn(todos)

        val result = todoRepository.syncTodosFromRemote()

        verify(todoRemoteDataSource).getTodos()
        verify(todoLocalDataSource).insertTodos(todos)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `uploadTodos should process pending uploads`() = runTest {
        val todos =
            listOf(todo.copy(id = UUID.randomUUID().toString(), isDeleted = false))
        `when`(todoLocalDataSource.getPendingUploadTodos()).thenReturn(todos)

        val flowResult = todoRepository.uploadTodos().first()

        verify(todoLocalDataSource).getPendingUploadTodos()
        assertTrue(flowResult is Result.Success)
    }
}