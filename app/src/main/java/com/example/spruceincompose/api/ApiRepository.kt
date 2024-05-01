package com.example.spruceincompose.api

import com.example.spruceincompose.utils.Resource
import com.example.spruceincompose.modals.CategoryModel
import com.example.spruceincompose.modals.PostModel

interface ApiRepository {
    suspend fun getPost(page: Int = 1, perPage: Int = 5): Resource<List<PostModel>>

    suspend fun getCategories(): Resource<List<CategoryModel>>

}