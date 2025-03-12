package com.example.todosomma

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SommaTodoApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }

}