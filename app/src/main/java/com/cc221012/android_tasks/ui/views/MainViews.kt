package com.cc221012.android_tasks.ui.views

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.flow.map


sealed class Tab(val route: String) {
    object CurrentTasks : Tab("currentTasks")
    object CompletedTasks : Tab("completedTasks")
}

@Composable
fun CurrentTasksView(tasks: List<Task>, onCheckboxClick: (Task) -> Unit) {
    LazyColumn {
        items(tasks.size) { index ->
            TaskListItem(tasks[index], onCheckboxClick)
        }
    }
}

@Composable
fun CompletedTasksView(tasks: List<Task>, onCheckboxClick: (Task) -> Unit) {
    LazyColumn {
        items(tasks.size) { index ->
            TaskListItem(tasks[index], onCheckboxClick)
        }
    }
}



@Composable
fun TaskListItem(task: Task, onCheckboxClick: (Task) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var isChecked by remember(task.id) { mutableStateOf(task.isCompleted) }

    key(task.id) {
        LaunchedEffect(isChecked) {
            if (isChecked != task.isCompleted) {
                coroutineScope.launch {
                    delay(200)
                    // Call onCheckboxClick to update the task's completion status
                    onCheckboxClick(task)
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
                modifier = Modifier
                    .weight(1f)
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
                    Log.d("MainView", "=================================================")
                    Log.d("MainView", "Checkbox clicked, new value: $newIsChecked")
                }
            )
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(mainViewModel: MainViewModel) {
    val coroutineScope = rememberCoroutineScope()
    Log.d("MainView", "MainView composed")

    val mainViewState by mainViewModel.tasksListState.collectAsState()
    Log.d("MainView", "MainView collecting tasksListState")

    var selectedTab by rememberSaveable { mutableStateOf<String>(Tab.CurrentTasks.route) }
    Log.d("MainView", "MainViewState: $mainViewState")
    Log.d("MainView", "=======================================================")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mainViewModel.showNewTaskWindow() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = when (selectedTab) {
                Tab.CurrentTasks.route -> 0
                Tab.CompletedTasks.route -> 1
                else -> 0
            }) {
                Tab(text = { Text("Current Tasks") }, selected = selectedTab == Tab.CurrentTasks.route, onClick = {
                    selectedTab = Tab.CurrentTasks.route
                    coroutineScope.launch {
                        mainViewModel.getTasksByCompletion(false)
                    }
                })
                Tab(text = { Text("Completed Tasks") }, selected = selectedTab == Tab.CompletedTasks.route, onClick = {
                    selectedTab = Tab.CompletedTasks.route
                    coroutineScope.launch {
                        mainViewModel.getTasksByCompletion(true)
                    }
                })}

            when (selectedTab) {
                Tab.CurrentTasks.route -> CurrentTasksView(mainViewState.tasks.filter { !it.isCompleted }, mainViewModel::updateTaskCompletionStatus)
                Tab.CompletedTasks.route -> CompletedTasksView(mainViewState.tasks.filter { it.isCompleted }, mainViewModel::updateTaskCompletionStatus)
                else -> {} // handle other cases
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