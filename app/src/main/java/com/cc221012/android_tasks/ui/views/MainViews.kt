package com.cc221012.android_tasks.ui.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cc221012.android_tasks.data.Task
import com.cc221012.android_tasks.ui.state.MainViewState
import com.cc221012.android_tasks.ui.viewModels.MainViewModel

sealed class Tab(val route: String) {
    object CurrentTasks : Tab("currentTasks")
    object CompletedTasks : Tab("completedTasks")
}

@Composable
fun CurrentTasksView(tasks: List<Task>) {
    LazyColumn {
        items(tasks.size) { index ->
            Text(text = tasks[index].name)
        }
    }
}

@Composable
fun CompletedTasksView(tasks: List<Task>) {
    LazyColumn {
        items(tasks.size) { index ->
            Text(text = tasks[index].name)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(mainViewModel: MainViewModel) {
    val mainViewState by mainViewModel.tasksListState.collectAsState()
    val selectedTabIndex = when (mainViewState.selectedTab) {
        is Tab.CurrentTasks -> 0
        is Tab.CompletedTasks -> 1
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Navigate to add task screen */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(text = { Text("Current Tasks") }, selected = selectedTabIndex == 0, onClick = { mainViewModel.updateSelectedTab(Tab.CurrentTasks) })
                Tab(text = { Text("Completed Tasks") }, selected = selectedTabIndex == 1, onClick = { mainViewModel.updateSelectedTab(Tab.CompletedTasks) })
            }

            when (mainViewState.selectedTab) {
                is Tab.CurrentTasks -> CurrentTasksView(mainViewState.tasks)
                is Tab.CompletedTasks -> CompletedTasksView(mainViewState.tasks.filter { it.isCompleted })
            }
        }
    }
}