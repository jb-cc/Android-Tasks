package com.cc221012.android_tasks.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cc221012.android_tasks.data.Task
import com.cc221012.android_tasks.data.TaskDao
import com.cc221012.android_tasks.ui.state.MainViewState
import com.cc221012.android_tasks.ui.views.Tab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainViewModel(private val dao: TaskDao): ViewModel() {

    // necessary variables for state
    private val _taskState = MutableStateFlow(Task("", null, false, null, null, false))
    val taskState: StateFlow<Task> = _taskState.asStateFlow()
    private val _taskListState = MutableStateFlow(MainViewState())
    val tasksListState: StateFlow<MainViewState> = _taskListState.asStateFlow()

    // CRUD Operations to be called from Dao


    fun createTask(taskName: String, taskDescription: String?, dueDate: LocalDate?, dueTime: LocalTime?) {
        val task = Task(
            name = taskName,
            description = taskDescription,
            dueDate = dueDate?.format(DateTimeFormatter.ISO_LOCAL_DATE),
            dueTime = dueTime?.format(DateTimeFormatter.ISO_LOCAL_TIME)
        )
        viewModelScope.launch {
            Log.d("MainViewModel", "Creating task with date: ${task.dueDate} and time: ${task.dueTime}")
            dao.createTask(task)
            getTasks()
        }
    }

    fun editTask(task: Task) {
        val updatedTask = task.copy(
            name = task.name,
            description = task.description,
            dueDate = task.dueDate?.format(DateTimeFormatter.ISO_LOCAL_DATE),
            dueTime = task.dueTime?.format(DateTimeFormatter.ISO_LOCAL_TIME)
        )
        viewModelScope.launch {
            dao.updateTask(updatedTask)
            getTasks()
        }
    }

    fun setTaskBeingEdited(task: Task) {
        Log.d("MainViewModel", "setTaskBeingEdited called with task: $task")
        _taskListState.value = _taskListState.value.copy(taskBeingEdited = task, editWindowOpened = true)
        Log.d("MainViewModel", "taskListState after setTaskBeingEdited: ${_taskListState.value}")
    }

    fun showEditWindow() {
        Log.d("MainViewModel", "showEditWindow called")
        _taskListState.value = _taskListState.value.copy(editWindowOpened = true)
        Log.d("MainViewModel", "taskListState after showEditWindow: ${_taskListState.value}")
    }

    fun hideEditWindow() {
        _taskListState.value = _taskListState.value.copy(taskBeingEdited = null, editWindowOpened = false)
    }
    // please for the love of god, work
    fun updateTaskCompletionStatus(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            dao.updateTask(updatedTask)
            getTasks()
        }
    }

    fun deleteTask(task: Task){
        viewModelScope.launch {
            dao.deleteTask(task)
            getTasks()
        }
    }

    fun getTasks(){
        viewModelScope.launch {
            dao.getTasks().collect { allTasks ->
                _taskListState.value = MainViewState(tasks = allTasks)
            }
        }
    }


    suspend fun getTasksByCompletion(isCompleted: Boolean){
        val tasks = dao.getTasksByStatus(isCompleted).first()
        _taskListState.value = MainViewState(tasks = tasks)
        Log.d("MainViewModel", "getTasksByCompletion | Fetched tasks: $tasks, Completion status: $isCompleted")
    }


//    fun updateSelectedTab(tab: Tab) {
//        _taskListState.value = _taskListState.value.copy(selectedTab = tab.route)
//    }

    fun showNewTaskWindow() {
        _taskListState.value = _taskListState.value.copy(newTaskWindowOpened = true)
    }

    fun hideNewTaskWindow() {
        _taskListState.value = _taskListState.value.copy(newTaskWindowOpened = false)
    }
}