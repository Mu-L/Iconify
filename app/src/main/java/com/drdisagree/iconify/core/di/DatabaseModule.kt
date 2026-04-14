package com.drdisagree.iconify.core.di

import com.drdisagree.iconify.data.dao.DynamicResourceDao
import com.drdisagree.iconify.data.database.DynamicResourceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(): DynamicResourceDatabase {
        return DynamicResourceDatabase.getInstance()
    }

    @Provides
    fun provideDynamicResourceDao(
        database: DynamicResourceDatabase
    ): DynamicResourceDao {
        return database.dynamicResourceDao()
    }
}