package com.plfdev.to_do_list.core.worker

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
            val result = syncTasksUseCases.invoke()
            if(result.isSuccess) {
                Log.d("BRATISLAV:","doWork Success")
                Result.success()
            } else {
                //IF GET 404 FOR EXAMPLE WE HAVE TO MERGE LIST, SYNC AND SEND ALL TO SERVER AGAIN.
                Log.d("BRATISLAV:","doWork Fail")
                Result.failure()
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
