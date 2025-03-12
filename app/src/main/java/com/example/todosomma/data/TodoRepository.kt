package com.example.todosomma.data

import android.content.Context
import com.example.todosomma.data.local.TodoLocalDataSource
import com.example.todosomma.data.model.Result
import com.example.todosomma.data.model.Todo
import com.example.todosomma.data.remote.TodoRemoteDataSource
import com.example.todosomma.util.NetworkUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface TodoRepository {
    val todos: Flow<List<Todo>>
    suspend fun insert(todo: Todo): Result<Unit>
    suspend fun delete(todo: Todo): Result<Unit>
    suspend fun update(todo: Todo): Result<Unit>
    suspend fun syncTodosFromRemote(): Result<Unit>
    suspend fun uploadTodos(): Flow<Result<Int>>
    suspend fun hasPendingUploadTodos(): Boolean
    suspend fun getPendingUploadTodos(): List<Todo>
}

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoLocalDataSource: TodoLocalDataSource,
    private val todoRemoteDataSource: TodoRemoteDataSource,
    private val networkUtils: NetworkUtils,
    private val context: Context
) :
    TodoRepository {

    override val todos = todoLocalDataSource.todos

    private var isSyncing = false

    override suspend fun insert(todo: Todo): Result<Unit> {
        if (networkUtils.isNetworkAvailable()) {
            try {

                val uploadMillis = System.currentTimeMillis()
                val response =
                    todoRemoteDataSource.createTodo(todo.copy(uploadedDate = uploadMillis))
                val serverId = response["name"]
                todoLocalDataSource.insert(
                    todo.copy(
                        serverId = serverId,
                        uploadedDate = uploadMillis
                    )
                )
                return Result.Success(Unit)
            } catch (e: Exception) {
                todoLocalDataSource.insert(todo)

                return Result.Error("$e")
            }
        } else {
            todoLocalDataSource.insert(todo)
            return Result.Success(Unit)
        }
    }

    override suspend fun delete(todo: Todo): Result<Unit> {
        if (networkUtils.isNetworkAvailable()) {
            try {
                if (todo.serverId != null) {
                    todoRemoteDataSource.deleteTodo(todo)
                    todoLocalDataSource.delete(todo)
                } else {
                    todoLocalDataSource.delete(todo);
                }
                return Result.Success(Unit)
            } catch (e: Exception) {
                todoLocalDataSource.softDelete(todo)
                return Result.Error("$e")
            }
        } else {
            todoLocalDataSource.softDelete(todo)
            return Result.Success(Unit)
        }
    }

    override suspend fun update(todo: Todo): Result<Unit> {
        if (networkUtils.isNetworkAvailable()) {
            try {
                val uploadMillis = System.currentTimeMillis()
                if (todo.serverId != null) {
                    todoRemoteDataSource.updateTodo(todo.copy(uploadedDate = uploadMillis))
                    todoLocalDataSource.update(
                        todo.copy(
                            uploadedDate = uploadMillis
                        )
                    )
                } else {
                    val response =
                        todoRemoteDataSource.createTodo(todo.copy(uploadedDate = uploadMillis))
                    val serverId = response["name"]
                    todoLocalDataSource.update(
                        todo.copy(
                            serverId = serverId,
                            uploadedDate = uploadMillis
                        )
                    )
                }
                return Result.Success(Unit)
            } catch (e: Exception) {
                todoLocalDataSource.update(todo)
                return Result.Error("$e")
            }
        } else {
            todoLocalDataSource.update(todo)
            return Result.Success(Unit)
        }
    }

    override suspend fun syncTodosFromRemote(): Result<Unit> {
        if (networkUtils.isNetworkAvailable()) {
            try {
                val todos = todoRemoteDataSource.getTodos()
                todoLocalDataSource.insertTodos(todos)
                return Result.Success(Unit)
            } catch (e: Exception) {
                return Result.Error("$e")
            }
        } else {
            return Result.Error("No internet connectivity")
        }
    }


    override suspend fun uploadTodos(): Flow<Result<Int>> = flow {
        if (!isSyncing) {
            isSyncing = true
            val todos = todoLocalDataSource.getPendingUploadTodos()
            var progressCount = 0
            todos.forEach { todo ->
                try {
                    if (todo.isDeleted) {
                        delete(todo)
                    } else if (todo.serverId == null) {
                        insert(todo)
                    } else {
                        update(todo)
                    }
                } catch (e: Exception) {
                    emit(Result.Error("${e.message}"))
                    delay(500)
                }
                emit(Result.Success(progressCount))
                progressCount++
            }
            isSyncing = false
        }
    }

    override suspend fun hasPendingUploadTodos(): Boolean {
        return todoLocalDataSource.getFirstPendingUploadTodo() != null
    }

    override suspend fun getPendingUploadTodos(): List<Todo> {
        return todoLocalDataSource.getPendingUploadTodos()
    }
}