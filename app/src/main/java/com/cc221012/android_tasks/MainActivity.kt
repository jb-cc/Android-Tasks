package com.cc221012.android_tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.cc221012.android_tasks.data.TasksDatabase
import com.cc221012.android_tasks.ui.viewModels.MainViewModel
import com.cc221012.android_tasks.ui.views.MainView
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(this, TasksDatabase::class.java, "TasksDatabase.db").build()
    }

    private val mainViewModel by viewModels<MainViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T{
                    return MainViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else MaterialTheme.colorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(mainViewModel)
                }
            }
        }
        lifecycleScope.launch {
            mainViewModel.getTasksByCompletion(false)
        }
    }
}