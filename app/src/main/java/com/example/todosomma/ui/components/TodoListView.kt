package com.example.todosomma.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todosomma.R
import com.example.todosomma.data.model.Todo
import com.example.todosomma.util.DateUtil


enum class TodoAction { OnTodoListItemTap, OnTodoItemCompleteTap }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListView(
    todos: List<Todo>,
    enabledInteraction: Boolean,
    isRefreshing: Boolean,
    onTap: (todo: Todo, TodoAction) -> Unit,
    onSwipeRefresh: () -> Unit
) {

    Column(modifier = Modifier
        .fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { onSwipeRefresh() },
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                content = {
                    itemsIndexed(todos) { index: Int, todo: Todo ->
                        TodoListItem(todo, onViewTap = {
                            onTap(todo, TodoAction.OnTodoListItemTap)
                        }, enabledInteraction = enabledInteraction,
                            onCompleteTap = {
                                onTap(todo, TodoAction.OnTodoItemCompleteTap)
                            }
                        )
                        if (index < todos.size - 1) {
                            HorizontalDivider(thickness = 2.dp, color = Color.Transparent)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}


@Composable
internal fun TodoListItem(
    todo: Todo,
    enabledInteraction: Boolean,
    onViewTap: () -> Unit,
    onCompleteTap: () -> Unit
) {

    val status =
        if (todo.completedDate != 0L) "Completed" else if (todo.dueDate < System.currentTimeMillis()) "Overdue" else "In Progress"

    val statusColor =
        if (todo.completedDate != 0L) colorResource(R.color.green) else if (todo.dueDate < System.currentTimeMillis()) Color.Red else colorResource(
            R.color.blue
        )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp)
            .background(
                color = if (enabledInteraction) Color.White else colorResource(R.color.white_70),
                shape = RoundedCornerShape(8.dp)
            )
            .height(64.dp)
            .padding(start = 8.dp, end = 16.dp)
            .clickable {
                if (enabledInteraction) {
                    onViewTap()
                }
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (enabledInteraction) {
                onCompleteTap()
            }
        }) {
            if (todo.completedDate == 0L) Icon(
                imageVector = Icons.Outlined.Circle,
                contentDescription = "Not Completed",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            ) else Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Completed",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(todo.title, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Not Completed",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = DateUtil.formatDateTimeMillis(todo.dueDate),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(color = Color.Black, shape = RoundedCornerShape(36.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor,
                )
            }
        }

        Icon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = "Open",
            tint = if (enabledInteraction) Color.Gray else Color.Transparent,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(20.dp)
        )
    }
}