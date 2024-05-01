package com.example.spruce.di


import com.example.spruceincompose.api.ApiRepoImpl
import com.example.spruceincompose.api.ApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideCategoryRepository(impl: ApiRepoImpl): ApiRepository = impl

}