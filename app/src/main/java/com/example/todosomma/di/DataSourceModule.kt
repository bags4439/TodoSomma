package com.example.todosomma.di

import com.example.todosomma.data.local.TodoLocalDataSource
import com.example.todosomma.data.local.TodoLocalDataSourceImpl
import com.example.todosomma.data.remote.TodoRemoteDataSource
import com.example.todosomma.data.remote.TodoRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    @Singleton
    @Binds
    fun bindsTodoLocalDataSource(todoLocalDataSource: TodoLocalDataSourceImpl): TodoLocalDataSource


    @Singleton
    @Binds
    fun bindsTodoRemoteDataSource(todoRemoteDataSource: TodoRemoteDataSourceImpl): TodoRemoteDataSource


}