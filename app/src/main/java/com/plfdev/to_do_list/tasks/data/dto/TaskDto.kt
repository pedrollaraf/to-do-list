package com.plfdev.to_do_list.tasks.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
)