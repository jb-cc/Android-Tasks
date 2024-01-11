package com.cc221012.android_tasks.ui.state

import com.cc221012.android_tasks.data.Task
import com.cc221012.android_tasks.ui.views.Tab

data class MainViewState(
    val tasks: List<Task> = emptyList(),
    val selectedTab: String = Tab.CurrentTasks.route,
    val newTaskWindowOpened: Boolean = false,
    val editWindowOpened: Boolean = false,
    val dueDateWindowOpened: Boolean = false
)