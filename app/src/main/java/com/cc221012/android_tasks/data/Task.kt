package com.cc221012.android_tasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String?,
    val isCompleted: Boolean = false,
    val dueDate: LocalDateTime? = null,
    val hasReminder: Boolean = false,
)
