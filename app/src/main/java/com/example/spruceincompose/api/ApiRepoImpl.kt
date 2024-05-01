package com.example.spruceincompose.api

import android.util.Log
import androidx.core.text.HtmlCompat
import com.example.spruceincompose.utils.Resource
import com.example.spruceincompose.modals.CategoryModel
import com.example.spruceincompose.modals.PostModel
import javax.inject.Inject

class ApiRepoImpl @Inject constructor(
    private val apiService : ApiService
) :
    ApiRepository {

    override suspend fun getPost(page: Int, perPage: Int): Resource<List<PostModel>> {
        return try {
            val result: List<PostModel> = apiService.getPosts(page = page, perPage = perPage)

            for (i in result.indices) {

                val catResult = apiService.getPostCategories(result[i].id)
                val tagResult = apiService.getPostTags(result[i].id)
                val imageResult = apiService.getPostImage(result[i].featured_media)

                result[i].content.rendered = HtmlCompat.fromHtml(
                    result[i].content.rendered.trim(),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                ).toString()
                result[i].cateResult = catResult
                result[i].tagsResult = tagResult
                result[i].imageResult = imageResult

            }

            Resource.Success(result)
        } catch (e: Exception) {
            Log.e("TAG", "getPost: ${e.message} ", )
            Resource.Failure(e)
        }
    }

    override suspend fun getCategories(): Resource<List<CategoryModel>> {
        return try {
            val result = apiService.getCategories()
            Resource.Success(result)
        } catch (e: Exception) {
            Log.e("TAG", "getCategories: ${e.message} ", )
            Resource.Failure(e)
        }
    }
}