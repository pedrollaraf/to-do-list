package com.plfdev.to_do_list.core.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases

class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val syncTasksUseCases: SyncTasksUseCases
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("BRATISLAV", "DO WORK")
            syncTasksUseCases.invoke()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
