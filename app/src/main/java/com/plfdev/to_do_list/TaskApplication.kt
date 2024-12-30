package com.plfdev.to_do_list

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.plfdev.to_do_list.core.worker.AppWorkerFactory
import com.plfdev.to_do_list.di.appModule
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TaskApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TaskApplication)
            androidLogger()
            modules(appModule)
        }

        val configuration = Configuration.Builder()
            .setWorkerFactory(AppWorkerFactory(get()))
            .build()
        WorkManager.initialize(this, configuration)
    }
}
