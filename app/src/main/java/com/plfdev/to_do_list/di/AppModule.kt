package com.plfdev.to_do_list.di

import androidx.work.WorkManager
import com.plfdev.to_do_list.BuildConfig
import com.plfdev.to_do_list.core.data.local.AppDatabaseFactory
import com.plfdev.to_do_list.core.data.networking.HttpClientFactory
import com.plfdev.to_do_list.core.data.networking.NetworkConnectivityObserver
import com.plfdev.to_do_list.tasks.data.repository.TaskRepositoryImpl
import com.plfdev.to_do_list.tasks.domain.repository.TaskRepository
import com.plfdev.to_do_list.tasks.domain.usecases.AddTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.GetTaskUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.SyncTasksUseCases
import com.plfdev.to_do_list.tasks.domain.usecases.UpdateTaskUseCases
import com.plfdev.to_do_list.tasks.presenter.viewmodel.TaskViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {
    single {
        HttpClientFactory.create(engine = CIO.create())
    }
    single {
        AppDatabaseFactory.create(context = androidContext()).taskDao()
    }
    single<TaskRepository> {
        TaskRepositoryImpl(
            taskDao = get(),
            httpClient = get(),
            baseUrl = BuildConfig.BASE_URL
        )
    }
    singleOf(::GetTaskUseCases)
    singleOf(::AddTaskUseCases)
    singleOf(::UpdateTaskUseCases)
    singleOf(::SyncTasksUseCases)
    single { NetworkConnectivityObserver(androidContext()) }
    // WorkManager
    single { WorkManager.getInstance(androidContext()) }
    viewModelOf(::TaskViewModel)
}

