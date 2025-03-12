package com.example.todosomma.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Todo(
    @PrimaryKey val id: String,
    val serverId: String? = null,
    val title: String,
    val description: String?,
    val createdBy: String,
    val createdDate: Long,
    val dueDate: Long,
    val uploadedDate: Long = 0,
    val completedDate: Long = 0,
    val isDeleted: Boolean = false
)