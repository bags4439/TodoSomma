package com.example.todosomma.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todosomma.data.model.Todo

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        const val DB_NAME = "My DB"
    }
}