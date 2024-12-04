package com.plfdev.to_do_list.tasks.data.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val isSynced: Boolean = false
)