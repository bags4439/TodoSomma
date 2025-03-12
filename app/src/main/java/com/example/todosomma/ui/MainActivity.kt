package com.example.todosomma.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todosomma.ui.components.DataSyncView
import com.example.todosomma.R
import com.example.todosomma.ui.components.EmptyScreenPlaceHolderView
import com.example.todosomma.ui.components.LoadingProgressView
import com.example.todosomma.ui.components.TodoAction
import com.example.todosomma.ui.components.TodoListView
import com.example.todosomma.ui.components.TodoView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val toDoViewModel: TodoViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                val uiState by toDoViewModel.uiState.collectAsStateWithLifecycle()

                val backgroundColor = MaterialTheme.colorScheme.primary
                val systemUiController = rememberSystemUiController()

                LaunchedEffect(Unit) {
                    systemUiController.setStatusBarColor(backgroundColor)
                    systemUiController.setNavigationBarColor(backgroundColor)
                }

                val sheetState = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()
                var showBottomSheet by remember { mutableStateOf(false) }
                val snackBarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor),
                    containerColor = backgroundColor,
                    floatingActionButton = {
                        if (!uiState.isSyncing && !uiState.isDownloadingFromRemote) {
                            ExtendedFloatingActionButton(
                                text = { Text(text = stringResource(R.string.create)) },
                                icon = {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Create Task"
                                    )
                                },
                                onClick = {
                                    toDoViewModel.selectedTodo = null
                                    showBottomSheet = true
                                }
                            )
                        }
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = snackBarHostState)
                    },
                ) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = innerPadding.calculateTopPadding()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        LaunchedEffect(uiState.error) {
                            uiState.error?.let {
                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        it,
                                        duration = SnackbarDuration.Long
                                    )
                                }
                            }
                        }

                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(16.dp)
                        )

                        if (uiState.hasPendingUploadItems) {
                            DataSyncView(
                                isSyncing = uiState.isSyncing,
                                progress = uiState.syncProgress,
                                max = uiState.maxSync
                            ) {
                                toDoViewModel.attemptUploadPendingTodos()
                            }
                        }

                        if (uiState.isLoading || uiState.isDownloadingFromRemote) {
                            LoadingProgressView()
                        }

                        if (uiState.todos.isEmpty() && !uiState.isLoading && !uiState.isDownloadingFromRemote) {
                            EmptyScreenPlaceHolderView(onCreateTap = {
                                toDoViewModel.selectedTodo = null
                                showBottomSheet = true
                            }, onDownloadTap = {
                                toDoViewModel.syncFromRemote()
                            })
                        }

                        TodoListView(
                            todos = uiState.todos,
                            enabledInteraction = !uiState.isSyncing && !uiState.isDownloadingFromRemote,
                            isRefreshing = uiState.isDownloadingFromRemote,
                            onTap = { todo, action ->
                                toDoViewModel.selectedTodo = null
                                if (action == TodoAction.OnTodoListItemTap) {
                                    toDoViewModel.selectedTodo = todo
                                    showBottomSheet = true
                                } else if (action == TodoAction.OnTodoItemCompleteTap) {
                                    toDoViewModel.toggleTodoCompleted(todo)
                                }
                            },
                            onSwipeRefresh = {
                                toDoViewModel.syncFromRemote()
                            }
                        )

                        if (showBottomSheet) {
                            ModalBottomSheet(
                                onDismissRequest = {
                                    showBottomSheet = false
                                },
                                sheetState = sheetState
                            ) {
                                TodoView(
                                    todo = toDoViewModel.selectedTodo,
                                    onCreateOrUpdate = { id, title, description, dueDate ->
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                        if (id != null) {
                                            //do update
                                            toDoViewModel.updateTodo(
                                                todo = toDoViewModel.selectedTodo!!,
                                                title = title,
                                                description = description,
                                                dueDate = dueDate
                                            )
                                        } else {
                                            //do create
                                            toDoViewModel.createTodo(
                                                title = title,
                                                description = description,
                                                dueDate = dueDate
                                            )
                                        }

                                        toDoViewModel.selectedTodo = null
                                    },
                                    onDelete = { todo ->
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                        toDoViewModel.removeTodo(todo)
                                    }, onCancel = {
                                        toDoViewModel.selectedTodo = null
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                    })
                            }
                        }
                    }
                }
            }
        }
    }

    fun doOpenTodo() {

    }
}