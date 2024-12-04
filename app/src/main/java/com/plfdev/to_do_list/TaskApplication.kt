package com.plfdev.to_do_list

import android.app.Application
import com.plfdev.to_do_list.di.appModule
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
    }
}
