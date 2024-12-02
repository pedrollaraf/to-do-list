package com.plfdev.to_do_list.tasks.domain.model

data class TaskUiModel(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
