package com.cc221012.android_tasks.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Task::class], version = 1)
abstract class TasksDatabase : RoomDatabase() {
    abstract val dao: TaskDao
}