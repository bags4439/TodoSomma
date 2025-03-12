package com.example.todosomma.data.local

import com.example.todosomma.data.model.Todo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TodoLocalDataSource {
    val todos: Flow<List<Todo>>
    suspend fun insert(todo: Todo)
    suspend fun insertTodos(todos: List<Todo>)
    suspend fun delete(todo: Todo)
    suspend fun softDelete(todo: Todo)
    suspend fun update(todo: Todo)
    suspend fun markAsCompleted(todoId: Int, completedDate: Long)
    suspend fun getFirstPendingUploadTodo(): Todo?
    suspend fun getUnUploadedTodos(): Flow<List<Todo>>
    suspend fun getPendingUploadTodos(): List<Todo>
}

@Singleton
class TodoLocalDataSourceImpl @Inject constructor(private val todoDao: TodoDao) :
    TodoLocalDataSource {
    override val todos: Flow<List<Todo>> = todoDao.getAll()

    override suspend fun insert(todo: Todo) {
        return todoDao.insert(todo)
    }

    override suspend fun insertTodos(todos: List<Todo>) {
        todoDao.insertTodos(todos)
    }

    override suspend fun delete(todo: Todo) {
        todoDao.delete(todo);
    }

    override suspend fun softDelete(todo: Todo) {
        todoDao.softDeleteTodo(todo.id)
    }

    override suspend fun update(todo: Todo) {
        todoDao.update(todo)
    }

    override suspend fun markAsCompleted(todoId: Int, completedDate: Long) {
        todoDao.markTodoAsCompleted(todoId, completedDate)
    }

    override suspend fun getFirstPendingUploadTodo(): Todo? {
        return todoDao.getFirstPendingUploadTodo()
    }

    override suspend fun getUnUploadedTodos(): Flow<List<Todo>> {
        return todoDao.getUnUploadedTodos()
    }

    override suspend fun getPendingUploadTodos(): List<Todo> {
        return todoDao.getPendingUploadTodos()
    }

}