package com.cc221012.android_tasks.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cc221012.android_tasks.data.Task
import com.cc221012.android_tasks.ui.state.MainViewState

sealed class Tab(val route: String) {
    object CurrentTasks : Tab("currentTasks")
    object CompletedTasks : Tab("completedTasks")
}

@Composable
fun CurrentTasksView(tasks: List<Task>) {
    // TODO: Implement the UI for the current tasks view
    Text(text = "Current Tasks")
}

@Composable
fun CompletedTasksView(tasks: List<Task>) {
    // TODO: Implement the UI for the completed tasks view
    Text(text = "Completed Tasks")
}

@Composable
fun MainView(mainViewState: MainViewState) {
    val selectedTabIndex = when (mainViewState.selectedTab) {
        is Tab.CurrentTasks -> 0
        is Tab.CompletedTasks -> 1
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(text = { Text("Current Tasks") }, selected = selectedTabIndex == 0, onClick = { /* TODO: Update the selected tab in the ViewModel */ })
            Tab(text = { Text("Completed Tasks") }, selected = selectedTabIndex == 1, onClick = { /* TODO: Update the selected tab in the ViewModel */ })
        }

        when (mainViewState.selectedTab) {
            is Tab.CurrentTasks -> CurrentTasksView(mainViewState.tasks)
            is Tab.CompletedTasks -> CompletedTasksView(mainViewState.tasks.filter { it.isCompleted })
        }
    }
}