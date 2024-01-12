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
import java.time.LocalDateTime

class MainViewModel(private val dao: TaskDao): ViewModel() {

    // necessary variables for state
    private val _taskState = MutableStateFlow(Task("", null, false, null, false, null, null))
    val taskState: StateFlow<Task> = _taskState.asStateFlow()
    private val _taskListState = MutableStateFlow(MainViewState())
    val tasksListState: StateFlow<MainViewState> = _taskListState.asStateFlow()

    // CRUD Operations to be called from Dao

    fun createTask(name: String, description: String?, dueDate: LocalDateTime?) {
        viewModelScope.launch {
            val task = Task(name = name, description = description, dueDate = dueDate)
            dao.createTask(task)
        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch {
            dao.updateTask(task)
            getTasks()
        }
    }

    fun setTaskBeingEdited(task: Task) {
        _taskListState.value = _taskListState.value.copy(taskBeingEdited = task, editWindowOpened = true)
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

    fun getTasksByCategory(category: String){
        viewModelScope.launch {
            dao.getTasksByCategory(category).collect { tasks ->
                _taskListState.value = MainViewState(tasks = tasks)
            }
        }
    }

    fun updateSelectedTab(tab: Tab) {
        _taskListState.value = _taskListState.value.copy(selectedTab = tab.route)
    }

    fun showNewTaskWindow() {
        _taskListState.value = _taskListState.value.copy(newTaskWindowOpened = true)
    }

    fun hideNewTaskWindow() {
        _taskListState.value = _taskListState.value.copy(newTaskWindowOpened = false)
    }
}