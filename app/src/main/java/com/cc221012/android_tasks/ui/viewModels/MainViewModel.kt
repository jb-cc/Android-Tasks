package com.cc221012.android_tasks.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cc221012.android_tasks.data.Task
import com.cc221012.android_tasks.data.TaskDao
import com.cc221012.android_tasks.ui.state.MainViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(private val dao: TaskDao): ViewModel() {

    // necessary variables for state
    private val _taskState = MutableStateFlow(Task("", null, false, null, false))
    val taskState: StateFlow<Task> = _taskState.asStateFlow()
    private val _tasks = MutableStateFlow(MainViewState())
    val tasks: StateFlow<MainViewState> = _tasks.asStateFlow()


    // CRUD Operations to be called from Dao

    fun createTask(task: Task){
        viewModelScope.launch {
            dao.createTask(task)
        }
    }

    fun updateTask(task: Task){
        viewModelScope.launch {
            dao.updateTask(task)
        }
    }

    fun deleteTask(task: Task){
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }
    fun getTasks(){
        viewModelScope.launch {
            dao.getTasks().collect() { allTasks -> }
        }
    }


}
