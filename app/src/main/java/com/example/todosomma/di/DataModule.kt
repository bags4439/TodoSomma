package com.example.todosomma.di

import com.example.todosomma.data.TodoRepository
import com.example.todosomma.data.TodoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsTodoRepository(todoRepository: TodoRepositoryImpl): TodoRepository
}