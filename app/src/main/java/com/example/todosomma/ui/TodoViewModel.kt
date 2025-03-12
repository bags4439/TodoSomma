package com.example.todosomma.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todosomma.data.model.Result
import com.example.todosomma.data.model.Todo
import com.example.todosomma.data.TodoRepository
import com.example.todosomma.data.network.SyncManager
import com.example.todosomma.notifications.TodoNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val syncManager: SyncManager,
    private val todoNotificationService: TodoNotificationService
) : ViewModel() {
    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    var selectedTodo: Todo? = null

    init {
        loadTodos()
        viewModelScope.launch {
            syncManager.startSyncing(viewModelScope) {
                attemptUploadPendingTodos()
            }
        }
    }

    fun attemptUploadPendingTodos() {
        viewModelScope.launch {
            if (!uiState.value.isSyncing && todoRepository.hasPendingUploadTodos()) {
                todoRepository.uploadTodos().onStart {
                    val pendingUploadTodos = todoRepository.getPendingUploadTodos()
                    _uiState.update {
                        it.copy(
                            hasPendingUploadItems = true,
                            isSyncing = true,
                            syncProgress = 0,
                            maxSync = pendingUploadTodos.size,
                        )
                    }
                }.onCompletion {
                    val hasPendingUploadItems = todoRepository.hasPendingUploadTodos()
                    _uiState.update {
                        it.copy(
                            hasPendingUploadItems = hasPendingUploadItems,
                            isSyncing = false,
                            syncProgress = 0,
                            maxSync = 0,
                        )
                    }
                }.catch {
                    _uiState.update {
                        it.copy(
                            error = it.error
                        )
                    }
                }.collect { result ->

                    if (result is Result.Error) {
                        _uiState.update {
                            it.copy(
                                error = result.message
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isSyncing = true,
                                syncProgress = (result as Result.Success<Int>).data,
                                error = null
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadTodos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            todoRepository.todos.catch { e -> _uiState.update { it.copy(error = "Error loading tasks") } }
                .onCompletion {

                }
                .collect { todos ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            todos = todos,
                            hasPendingUploadItems = todoRepository.hasPendingUploadTodos(),
                        )
                    }
                }
        }
    }

    fun createTodo(title: String, description: String?, dueDate: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    hasPendingUploadItems = true,
                    isSyncing = true,
                    syncProgress = 0,
                    maxSync = 1,
                    error = null
                )
            }

            val todo = Todo(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                createdBy = "Somma Dev",
                createdDate = System.currentTimeMillis(),
                dueDate = dueDate
            )
            val result = todoRepository.insert(
                todo
            )

            todoNotificationService.scheduleNotification(todo = todo)

            var errorMessage: String? = null

            if (result is Result.Error) {
                errorMessage = result.message
            }

            val hasPendingUploadItems = todoRepository.hasPendingUploadTodos()
            _uiState.update {
                it.copy(
                    hasPendingUploadItems = hasPendingUploadItems,
                    isSyncing = false,
                    syncProgress = 0,
                    maxSync = 0,
                    error = errorMessage
                )
            }
        }
    }

    fun updateTodo(todo: Todo, title: String, description: String?, dueDate: Long) {

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    hasPendingUploadItems = true,
                    isSyncing = true,
                    syncProgress = 0,
                    maxSync = 1,
                    error = null
                )
            }

            val result = todoRepository.update(
                todo.copy(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    completedDate = 0,
                    uploadedDate = 0
                )
            )

            todoNotificationService.updateNotification(todo = todo)

            var errorMessage: String? = null

            if (result is Result.Error) {
                errorMessage = result.message
            }

            val hasPendingUploadItems = todoRepository.hasPendingUploadTodos()
            _uiState.update {
                it.copy(
                    hasPendingUploadItems = hasPendingUploadItems,
                    isSyncing = false,
                    syncProgress = 0,
                    maxSync = 0,
                    error = errorMessage
                )
            }
        }
    }


    fun removeTodo(todo: Todo) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    hasPendingUploadItems = true,
                    isSyncing = true,
                    syncProgress = 0,
                    maxSync = 1,
                    error = null
                )
            }

            val result = todoRepository.delete(todo.copy(isDeleted = true, uploadedDate = 0))

            todoNotificationService.cancelNotification(todoId = todo.id)

            var errorMessage: String? = null

            if (result is Result.Error) {
                errorMessage = result.message
            }

            val hasPendingUploadItems = todoRepository.hasPendingUploadTodos()
            _uiState.update {
                it.copy(
                    hasPendingUploadItems = hasPendingUploadItems,
                    isSyncing = false,
                    syncProgress = 0,
                    maxSync = 0,
                    error = errorMessage
                )
            }
        }
    }

    fun toggleTodoCompleted(todo: Todo) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    hasPendingUploadItems = true,
                    isSyncing = true,
                    syncProgress = 0,
                    maxSync = 1
                )
            }

            val updatedTodo = todo.copy(
                completedDate = if (todo.completedDate != 0L) 0L else System.currentTimeMillis(),
                uploadedDate = 0
            )
            val result = todoRepository.update(
                updatedTodo
            )

            if (updatedTodo.completedDate != 0L) {
                todoNotificationService.cancelNotification(todoId = todo.id)
            }

            var errorMessage: String? = null

            if (result is Result.Error) {
                errorMessage = result.message
            }

            val hasPendingUploadItems = todoRepository.hasPendingUploadTodos()
            _uiState.update {
                it.copy(
                    hasPendingUploadItems = hasPendingUploadItems,
                    isSyncing = false,
                    syncProgress = 0,
                    maxSync = 0,
                    error = errorMessage
                )
            }
        }
    }


    fun syncFromRemote() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDownloadingFromRemote = true,
                )
            }

            todoRepository.syncTodosFromRemote()

            _uiState.update {
                it.copy(
                    isDownloadingFromRemote = false,
                )
            }
        }
    }
}