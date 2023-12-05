package com.cc221012.android_tasks.ui.views

sealed class Tab (val route: String){
    object CurrentTasks: Tab("currentTasks")
    object CompletedTasks: Tab("completedTasks")
}