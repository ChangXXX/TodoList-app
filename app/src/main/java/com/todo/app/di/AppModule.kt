package com.todo.app.di

import android.content.Context
import androidx.room.Room
import com.todo.app.api.TodoService
import com.todo.app.common.AssetLoader
import com.todo.app.data.database.AppDatabase
import com.todo.app.data.database.TodoDao
import com.todo.app.data.repository.asset.TodoAssetDataSource
import com.todo.app.data.repository.asset.TodoAssetRepository
import com.todo.app.data.repository.asset.TodoDataSource
import com.todo.app.data.repository.local.TodoLocalDataSource
import com.todo.app.data.repository.local.TodoLocalRepository
import com.todo.app.data.repository.remote.TodoRemoteDataSource
import com.todo.app.data.repository.remote.TodoRemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideIoDispatcher() = Dispatchers.IO

    // Step 1 json asset load
    @Provides
    @Singleton
    fun providesAssetLoader(@ApplicationContext appContext: Context): AssetLoader =
        AssetLoader(appContext)

    @Provides
    @Singleton
    fun provideTodoAssetDataSource(assetLoader: AssetLoader): TodoDataSource {
        return TodoAssetDataSource(assetLoader)
    }

    @Provides
    @Singleton
    fun provideTodoAssetRepository(assetDataSource: TodoAssetDataSource): TodoAssetRepository {
        return TodoAssetRepository(assetDataSource)
    }

    // Step 2 Room
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "Todos.db"
        ).build()
    }

    @Provides
    fun provideTodoDao(appDatabase: AppDatabase): TodoDao {
        return appDatabase.todoDao()
    }

    @Provides
    @Singleton
    fun providesTodoLocalDataSource(todoDao: TodoDao): com.todo.app.data.repository.TodoDataSource {
        return TodoLocalDataSource(todoDao)
    }

    @Provides
    @Singleton
    fun providesTodoLocalRepository(
        localDataSource: TodoLocalDataSource,
        ioDispatcher: CoroutineDispatcher
    ): TodoLocalRepository {
        return TodoLocalRepository(localDataSource, ioDispatcher)
    }

    // step 3 retrofit
    @Provides
    @Singleton
    fun providesTodoService(): TodoService {
        return TodoService.create()
    }

    @Provides
    @Singleton
    fun providesTodoRemoteDataSource(todoService: TodoService): com.todo.app.data.repository.TodoDataSource {
        return TodoRemoteDataSource(todoService)
    }

    @Provides
    @Singleton
    fun providesTodoRemoteRepository(todoRemoteDataSource: TodoRemoteDataSource): TodoRemoteRepository {
        return TodoRemoteRepository(todoRemoteDataSource)
    }
}