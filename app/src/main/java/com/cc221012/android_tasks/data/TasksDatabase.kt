package com.cc221012.android_tasks.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cc221012.android_tasks.models.Converters


@Database(entities = [Task::class], version = 1)
@TypeConverters(Converters::class) // Whenever Room sees the Datatypes it should convert, it does so
abstract class TasksDatabase : RoomDatabase() {
    abstract val dao: TaskDao
}