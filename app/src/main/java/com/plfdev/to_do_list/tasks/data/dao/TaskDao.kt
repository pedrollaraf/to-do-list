package com.plfdev.to_do_list.tasks.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.plfdev.to_do_list.tasks.data.entitiy.TaskEntity
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    suspend fun getTasks(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE isSynced = 0")
    suspend fun getUnSyncedTasks(): List<TaskEntity>

    @Query("UPDATE tasks SET isSynced = 1 WHERE id = :taskId")
    suspend fun markAsSynced(taskId: Long)
}