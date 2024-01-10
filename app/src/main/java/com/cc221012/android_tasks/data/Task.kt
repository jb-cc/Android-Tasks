package com.cc221012.android_tasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity (tableName = "tasks")
data class Task(

    val name: String,
    val description: String?,
    val isCompleted: Boolean = false,
    val dueDate: LocalDateTime? = null,
    val hasReminder: Boolean = false,
    val reminderDate: LocalDateTime? = null,
    val category: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
