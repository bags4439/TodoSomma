package com.example.todosomma.notifications

import android.content.Context
import androidx.work.*
import com.example.todosomma.data.model.Todo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TodoNotificationService @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleNotification(todo: Todo) {
        val delay = todo.dueDate - System.currentTimeMillis()
        if (delay <= 0) return

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("TODO_ID" to todo.id, "TODO_TITLE" to todo.title))
            .build()

        workManager.enqueueUniqueWork(
            "todo_notification_${todo.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelNotification(todoId: String) {
        workManager.cancelUniqueWork("todo_notification_$todoId")
    }

    fun updateNotification(todo: Todo) {
        cancelNotification(todo.id)
        scheduleNotification(todo)
    }
}
