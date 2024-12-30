package com.plfdev.to_do_list.core.worker

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases

class AppWorkerFactory(
    private val syncTasksUseCases: SyncTasksUseCases
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        Log.d("BRATISLAV", "createWorker")
        return when (workerClassName) {
            SyncWorker::class.java.name ->
                SyncWorker(appContext, workerParameters, syncTasksUseCases)
            else -> null
        }
    }
}