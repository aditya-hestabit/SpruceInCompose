package com.example.spruceincompose.api

import com.example.spruceincompose.modals.CategoryModel
import com.example.spruceincompose.modals.PostImageModel
import com.example.spruceincompose.modals.PostModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("wp-json/wp/v2/posts")
    suspend fun getPosts(
        @Query("per_page") perPage: Int = 5,
        @Query("page") page: Int = 1
    ): List<PostModel>

    @GET("index.php//wp-json/wp/v2/categories")
    suspend fun getPostCategories(
        @Query("post") postId: Int
    ): List<CategoryModel>

    @GET("index.php/wp-json/wp/v2/tags")
    suspend fun getPostTags(
        @Query("post") postId: Int
    ): List<CategoryModel>

    @GET("index.php/wp-json/wp/v2/media/{id}")
    suspend fun getPostImage(
        @Path("id") mediaId: Int
    ): PostImageModel

    @GET("wp-json/wp/v2/categories")
    suspend fun getCategories(): List<CategoryModel>

}