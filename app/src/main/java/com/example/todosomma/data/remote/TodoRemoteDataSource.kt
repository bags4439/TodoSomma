package com.example.todosomma.data.remote

import com.example.todosomma.data.model.Todo
import javax.inject.Inject
import javax.inject.Singleton


interface TodoRemoteDataSource {
    suspend fun createTodo(todo: Todo): Map<String, String>
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
    suspend fun getTodos(): List<Todo>
}

@Singleton
class TodoRemoteDataSourceImpl @Inject constructor(private val remoteApiService: TodoApiService) :
    TodoRemoteDataSource {
    override suspend fun createTodo(todo: Todo): Map<String, String> {
        val response = remoteApiService.createTodo(todo)

        return response
    }

    override suspend fun updateTodo(todo: Todo) {
        remoteApiService.updateTodo(serverId = todo.serverId!!, todo = todo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        remoteApiService.deleteTodo(serverId = todo.serverId!!)
    }

    override suspend fun getTodos(): List<Todo> {
        return remoteApiService.getTodos().map { (serverId, todo) ->
            todo.copy(serverId = serverId)
        }.toList()
    }


}