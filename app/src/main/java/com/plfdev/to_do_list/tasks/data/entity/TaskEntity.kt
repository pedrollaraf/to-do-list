package com.plfdev.to_do_list.tasks.data.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val isAdded: Boolean,
    val isUpdated: Boolean,
    val isDeleted: Boolean,
    val isSynced: Boolean,
)
