package com.cc221012.android_tasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "tasks")
data class Task(
    val name: String,
    val description: String?,
    var isCompleted: Boolean = false,
    val dueDate: String? = null,
    val dueTime: String? = null,
    val hasReminder: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
