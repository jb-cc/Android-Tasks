package com.cc221012.android_tasks.ui.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cc221012.android_tasks.data.Task
import com.cc221012.android_tasks.ui.viewModels.MainViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

sealed class Tab(val route: String) {
    object CurrentTasks : Tab("currentTasks")
    object CompletedTasks : Tab("completedTasks")
}

@Composable
fun CurrentTasksView(tasks: List<Task>, onCheckboxClick: (Task) -> Unit, mainViewModel: MainViewModel) {
    LazyColumn {
        items(tasks.size) { index ->
            TaskListItem(
                task = tasks[index],
                onCheckboxClick = onCheckboxClick,
                mainViewModel
            )
        }
    }
}

@Composable
fun CompletedTasksView(tasks: List<Task>, onCheckboxClick: (Task) -> Unit, mainViewModel: MainViewModel) {
    LazyColumn {
        items(tasks.size) { index ->
            TaskListItem(
                task = tasks[index],
                onCheckboxClick = onCheckboxClick,
                mainViewModel
            )
        }
    }
}


@Composable
fun TaskListItem(task: Task, onCheckboxClick: (Task) -> Unit, mainViewModel: MainViewModel) {


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
                .padding(16.dp)
                .clickable {
                    mainViewModel.setTaskBeingEdited(task)
                    mainViewModel.hideNewTaskWindow()
                    mainViewModel.showEditWindow()
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { newIsChecked ->
                    isChecked = newIsChecked
                    Log.d("MainView", "Checkbox clicked, new value: $newIsChecked")
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(task.name, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold))
                if (task.description != null) {
                    Text(task.description, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Light))
                }
                if (task.dueDate != null || task.dueTime != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                            .padding(4.dp)
                    ) {
                        Row {
                            if (task.dueDate != null) {
                                val date = LocalDate.parse(task.dueDate, DateTimeFormatter.ISO_LOCAL_DATE)
                                val formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Due date and time")
                                    Text(" $formattedDate")
                                }
                            }
                            if (task.dueTime != null) {
                                val time = LocalTime.parse(task.dueTime, DateTimeFormatter.ISO_LOCAL_TIME)
                                val formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(", $formattedTime")
                                }
                            }
                        }
                    }
                }
            }
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
            FloatingActionButton(onClick = { mainViewModel.showNewTaskWindow() },
                modifier = Modifier.width(110.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = "Add Task")
                    Text("Add Task")
                }
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Tasks", modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp), style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Normal))
            TabRow(
                selectedTabIndex = when (selectedTab) {
                    Tab.CurrentTasks.route -> 0
                    Tab.CompletedTasks.route -> 1
                    else -> 0
                }
            ) {
                Tab(
                    text = { Text("Current") },
                    selected = selectedTab == Tab.CurrentTasks.route,
                    onClick = {
                        selectedTab = Tab.CurrentTasks.route
                        coroutineScope.launch {
                            mainViewModel.getTasksByCompletion(false)
                        }
                    }
                )
                Tab(
                    text = { Text("Completed") },
                    selected = selectedTab == Tab.CompletedTasks.route,
                    onClick = {
                        selectedTab = Tab.CompletedTasks.route
                        coroutineScope.launch {
                            mainViewModel.getTasksByCompletion(true)
                        }
                    }
                )
            }

            when (selectedTab) {
                Tab.CurrentTasks.route -> CurrentTasksView(
                    mainViewState.tasks.filter { !it.isCompleted },
                    mainViewModel::updateTaskCompletionStatus,
                    mainViewModel
                )

                Tab.CompletedTasks.route -> CompletedTasksView(
                    mainViewState.tasks.filter { it.isCompleted },
                    mainViewModel::updateTaskCompletionStatus,
                    mainViewModel
                )
            }
        }
    }


    var dueDate by remember { mutableStateOf<LocalDate?>(null) }
    var dueTime by remember { mutableStateOf<LocalTime?>(null) }
    var editDueDate by remember { mutableStateOf<LocalDate?>(null) }
    var editDueTime by remember { mutableStateOf<LocalTime?>(null) }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    if (mainViewState.newTaskWindowOpened) {
        Dialog(onDismissRequest = { mainViewModel.hideNewTaskWindow() }) {
            Column {
                var taskName by remember { mutableStateOf("") }
                var taskDescription by remember { mutableStateOf("") }

                TextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") }
                )
                TextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Task Description") }
                )

                Row {
                    Row {
                        IconButton(onClick = { dateDialogState.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                        }
                        IconButton(onClick = { timeDialogState.show() }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Pick Time")
                        }
                        IconButton(onClick = {
                            mainViewModel.createTask(taskName, taskDescription, dueDate, dueTime)
                            mainViewModel.hideNewTaskWindow()
                        }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add Task")
                        }
                    }
                }
            }
        }



        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(text = "Ok") {
                    dateDialogState.hide()
                }
                negativeButton(text = "Cancel") {
                    dateDialogState.hide()
                }
            }
        ) {
            datepicker(
                initialDate = LocalDate.now(),
                title = "Pick a date",
                allowedDateValidator = { it.isAfter(LocalDate.now().minusDays(1)) }
            ) { date ->
                dueDate = date
            }
        }

        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(text = "Ok") {
                    timeDialogState.hide()
                }
                negativeButton(text = "Cancel") {
                    timeDialogState.hide()
                }
            }
        ) {
            timepicker(
                initialTime = LocalTime.NOON,
                title = "Pick a time",
                timeRange = LocalTime.MIDNIGHT..LocalTime.MAX
            ) { time ->
                dueTime = time
            }
        }
    }


    if (mainViewState.editWindowOpened) {
        val taskBeingEdited = mainViewState.taskBeingEdited
        if (taskBeingEdited != null) {
            // Set editDueDate and editDueTime to the due date and due time of the task being edited
            editDueDate = taskBeingEdited.dueDate?.let {
                LocalDate.parse(
                    it,
                    DateTimeFormatter.ISO_LOCAL_DATE
                )
            }
            editDueTime = taskBeingEdited.dueTime?.let {
                LocalTime.parse(
                    it,
                    DateTimeFormatter.ISO_LOCAL_TIME
                )
            }

            Dialog(onDismissRequest = { mainViewModel.hideEditWindow() }) {
                Column {
                    var taskName by remember { mutableStateOf(taskBeingEdited.name) }
                    var taskDescription by remember {
                        mutableStateOf(
                            taskBeingEdited.description ?: ""
                        )
                    }

                    TextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = { Text("Task Name") }
                    )
                    TextField(
                        value = taskDescription,
                        onValueChange = { taskDescription = it },
                        label = { Text("Task Description") }
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { dateDialogState.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                        }
                        IconButton(onClick = { timeDialogState.show() }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Pick Time")
                        }
                        IconButton(onClick = {
                            val updatedTask = taskBeingEdited.copy(
                                name = taskName,
                                description = taskDescription,
                                dueDate = editDueDate?.format(DateTimeFormatter.ISO_LOCAL_DATE),
                                dueTime = editDueTime?.format(DateTimeFormatter.ISO_LOCAL_TIME)
                            )
                            mainViewModel.editTask(updatedTask)
                            mainViewModel.hideEditWindow()
                        }) {
                            Icon(Icons.Default.Done, contentDescription = "Update Task")
                        }
                        Spacer(modifier = Modifier.width(92.dp))
                        IconButton(onClick = {
                            mainViewModel.deleteTask(taskBeingEdited)
                            mainViewModel.hideEditWindow()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
                        }
                    }
                }
            }
            MaterialDialog(
                dialogState = dateDialogState,
                buttons = {
                    positiveButton(text = "Ok") {
                        dateDialogState.hide()
                    }
                    negativeButton(text = "Cancel") {
                        dateDialogState.hide()
                    }
                }
            ) {
                datepicker(
                    initialDate = editDueDate ?: LocalDate.now(),
                    title = "Pick a date",
                    allowedDateValidator = { it.isAfter(LocalDate.now().minusDays(1)) }
                ) { date ->
                    editDueDate = date
                }
            }

            MaterialDialog(
                dialogState = timeDialogState,
                buttons = {
                    positiveButton(text = "Ok") {
                        timeDialogState.hide()
                    }
                    negativeButton(text = "Cancel") {
                        timeDialogState.hide()
                    }
                }
            ) {
                timepicker(
                    initialTime = editDueTime ?: LocalTime.NOON,
                    title = "Pick a time",
                    timeRange = LocalTime.MIDNIGHT..LocalTime.MAX
                ) { time ->
                    editDueTime = time
                }
            }
        }
    }
}
