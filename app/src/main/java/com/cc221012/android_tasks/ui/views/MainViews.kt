package com.cc221012.android_tasks.ui.views

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cc221012.android_tasks.data.Task
import com.cc221012.android_tasks.ui.state.MainViewState
import com.cc221012.android_tasks.ui.viewModels.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import androidx.compose.runtime.key
import kotlinx.coroutines.flow.map


sealed class Tab(val route: String) {
    object CurrentTasks : Tab("currentTasks")
    object CompletedTasks : Tab("completedTasks")
}

@Composable
fun CurrentTasksView(tasks: List<Task>, onTaskClick: (Task) -> Unit) {
    LazyColumn {
        items(tasks.size) { index ->
            TaskListItem(tasks[index], onTaskClick)
        }
    }
}

@Composable
fun CompletedTasksView(tasks: List<Task>, onTaskClick: (Task) -> Unit) {
    LazyColumn {
        items(tasks.size) { index ->
            TaskListItem(tasks[index], onTaskClick)
        }
    }
}



@Composable
fun TaskListItem(task: Task, onTaskClick: (Task) -> Unit) {
    key(task.id) {
        val coroutineScope = rememberCoroutineScope()
        var isChecked by remember { mutableStateOf(task.isCompleted) }

        LaunchedEffect(isChecked) {
            if (isChecked != task.isCompleted) {
                coroutineScope.launch {
                    delay(200)
                    // Create a new Task object with the updated isCompleted property
                    val updatedTask = task.copy(isCompleted = isChecked)
                    onTaskClick(updatedTask)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.clickable { onTaskClick(task) } // Apply clickable to the Column
            ) {
                Text(task.name)
                if (task.description != null) {
                    Text(task.description)
                }
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = { newIsChecked ->
                    isChecked = newIsChecked
                }
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(mainViewModel: MainViewModel) {
    val mainViewState by mainViewModel.tasksListState.collectAsState()
    var selectedTab by remember { mutableStateOf<Tab>(Tab.CurrentTasks) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mainViewModel.showNewTaskWindow() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = when (selectedTab) {
                is Tab.CurrentTasks -> 0
                is Tab.CompletedTasks -> 1
            }) {
                Tab(text = { Text("Current Tasks") }, selected = selectedTab is Tab.CurrentTasks, onClick = { selectedTab = Tab.CurrentTasks; mainViewModel.updateSelectedTab(Tab.CurrentTasks); mainViewModel.getTasksByCompletion(false) })
                Tab(text = { Text("Completed Tasks") }, selected = selectedTab is Tab.CompletedTasks, onClick = { selectedTab = Tab.CompletedTasks; mainViewModel.updateSelectedTab(Tab.CompletedTasks); mainViewModel.getTasksByCompletion(true) })
            }

            when (mainViewState.selectedTab) {
                is Tab.CurrentTasks -> CurrentTasksView(mainViewState.tasks, mainViewModel::updateTask)
                is Tab.CompletedTasks -> CompletedTasksView(mainViewState.tasks.filter { it.isCompleted }, mainViewModel::updateTask)
            }
        }
    }





    if (mainViewState.newTaskWindowOpened) {
        Dialog(onDismissRequest = { mainViewModel.hideNewTaskWindow() }) {
            // Replace this TODO with the content of the dialog
            Column {
                var taskName by remember { mutableStateOf("") }
                var taskDescription by remember { mutableStateOf("") }
                var dueDate by remember { mutableStateOf<LocalDateTime?>(null) }

                TextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") }
                )
                TextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Task Description (optional)") }
                )
                // TODO: Add a date picker for the due date
                Button(onClick = {
                    mainViewModel.createTask(taskName, taskDescription, dueDate)
                    mainViewModel.hideNewTaskWindow()
                }) {
                    Text("Add Task")
                }
                Button(onClick = { mainViewModel.hideNewTaskWindow() }) {
                    Text("Cancel")
                }
            }
        }
    }
}