package com.plfdev.to_do_list.tasks.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Task(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
    @Transient val isAdded: Boolean = false,
    @Transient val isUpdated: Boolean = false,
    @Transient val isSynced: Boolean = false
)

