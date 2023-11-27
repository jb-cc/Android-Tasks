package com.cc221012.android_tasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var description: String?,
    var isCompleted: Boolean = false,
    var dueDate: LocalDateTime? = null,
    var hasReminder: Boolean = false,
)
