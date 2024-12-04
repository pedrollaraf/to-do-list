package com.plfdev.to_do_list.tasks.domain.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
)
