package com.example.spruceincompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spruceincompose.api.ApiRepository
import com.example.spruceincompose.modals.CategoryModel
import com.example.spruceincompose.modals.PostModel
import com.example.spruceincompose.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class SpruceViewModel @Inject constructor(private val apiRepository: ApiRepository) : ViewModel() {

    private val _postDataFlow = MutableStateFlow<Resource<List<PostModel>>?>(null)
    val postDataFlow: StateFlow<Resource<List<PostModel>>?> = _postDataFlow
    private val _categoriesFlow = MutableStateFlow<Resource<List<CategoryModel>>?>(null)
    val categoryFlow: StateFlow<Resource<List<CategoryModel>>?> = _categoriesFlow

    var isLastPageReached = false

    var mPage = 1

    private fun getPostData(page: Int = mPage, perPage: Int = 15) = viewModelScope.launch {
        if (!isLastPageReached) {
            _postDataFlow.value = Resource.Loading
            val result: Resource<List<PostModel>> =
                apiRepository.getPost(page = page, perPage = perPage)
            _postDataFlow.value = result
        } else {
            _postDataFlow.value = Resource.Failure(Exception("No More Content"))
        }
    }

    private fun getCategories() = viewModelScope.launch {
        _categoriesFlow.value = Resource.Loading
        val result = apiRepository.getCategories()
        _categoriesFlow.value = result
    }

    init {
        getCategories()
    }

    init {
        if (_postDataFlow.value == null)
            getPostData()
    }

}