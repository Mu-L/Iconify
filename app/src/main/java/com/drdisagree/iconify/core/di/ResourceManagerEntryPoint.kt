package com.drdisagree.iconify.core.di

import com.drdisagree.iconify.data.repository.DynamicResourceRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ResourceManagerEntryPoint {

    fun repository(): DynamicResourceRepository
}