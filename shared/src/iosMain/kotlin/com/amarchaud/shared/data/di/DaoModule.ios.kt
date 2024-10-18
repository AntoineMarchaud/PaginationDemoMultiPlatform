package com.amarchaud.shared.data.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.amarchaud.shared.data.db.PaginationDemoDb
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSFileManager

private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

actual val daoModule: Module = module {
    single<RoomDatabase.Builder<PaginationDemoDb>> {
        val dbFilePath =  documentDirectory() + "/my_room.db"
        Room.databaseBuilder<PaginationDemoDb>(
            name = dbFilePath,
        )
    }
}
