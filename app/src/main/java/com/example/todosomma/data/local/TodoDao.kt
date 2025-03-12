package com.example.todosomma.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todosomma.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo WHERE isDeleted = 0 ORDER BY dueDate DESC")
    fun getAll(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<Todo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("UPDATE todo SET isDeleted = 1, uploadedDate = null WHERE id = :todoId")
    suspend fun softDeleteTodo(todoId: String)

    @Query("UPDATE todo SET completedDate = :completedDate WHERE id = :todoId")
    suspend fun markTodoAsCompleted(todoId: Int, completedDate: Long)

    @Query("SELECT * FROM todo WHERE uploadedDate is  0 ORDER BY createdDate DESC")
    fun getUnUploadedTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todo WHERE uploadedDate is  0 LIMIT 1")
    suspend fun getFirstPendingUploadTodo(): Todo?

    @Query("SELECT * FROM todo WHERE uploadedDate is  0")
    suspend fun getPendingUploadTodos(): List<Todo>
}