package com.plfdev.to_do_list.core.data.local

import android.content.Context
import androidx.room.Room

object AppDatabaseFactory {
    fun create(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "task-db"
        ).fallbackToDestructiveMigration().build()
    }
}