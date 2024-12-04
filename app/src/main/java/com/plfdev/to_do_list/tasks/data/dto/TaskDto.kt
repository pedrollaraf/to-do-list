package com.plfdev.to_do_list.tasks.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val description: String?,
    val isCompleted: Boolean
)