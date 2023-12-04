package com.cc221012.android_tasks.ui

import androidx.lifecycle.ViewModel
import com.cc221012.android_tasks.data.Task
import com.cc221012.android_tasks.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(private val dao: TaskDao): ViewModel() {
    private val _taskState = MutableStateFlow(Task("", null, false, null, false))
    val taskState: StateFlow<Task> = _taskState.asStateFlow()

}