package com.example.todosomma.ui

import com.example.todosomma.data.model.Todo

data class TodoUiState(
    val isLoading: Boolean = false,
    val todos: List<Todo> = emptyList(),
    val error: String? = null,
    val isSyncing: Boolean = false,
    val isDownloadingFromRemote: Boolean = false,
    val hasPendingUploadItems: Boolean = false,
    val syncProgress: Int = 0,
    val maxSync: Int = 0
)