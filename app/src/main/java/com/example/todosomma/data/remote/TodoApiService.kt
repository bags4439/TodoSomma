package com.example.todosomma.data.remote

import com.example.todosomma.data.model.Todo
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TodoApiService {
    @POST("todos.json")
    suspend fun createTodo(@Body todo: Todo): Map<String, String>

    @PATCH("todos/{serverId}.json")
    suspend fun updateTodo(@Path("serverId") serverId: String, @Body todo: Todo)

    @DELETE("todos/{serverId}.json")
    suspend fun deleteTodo(@Path("serverId") serverId: String)

    @GET("todos.json")
    suspend fun getTodos(): Map<String, Todo>
}