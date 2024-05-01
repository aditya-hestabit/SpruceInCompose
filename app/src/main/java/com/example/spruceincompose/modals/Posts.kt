package com.example.spruceincompose.modals



data class PostModel(
    val id: Int = 0,
    val date: String = "",
    val title: PostTitle = PostTitle(),
    val content: PostContent = PostContent(),
    val featured_media: Int = 0,
    val categories: List<Int> = emptyList(),
    val tags: List<Int> = emptyList(),
    var cateResult: List<CategoryModel> = emptyList(),
    var tagsResult: List<CategoryModel> = emptyList(),
    var imageResult: PostImageModel = PostImageModel()
)

data class PostTitle(val rendered: String = "")

data class PostContent(var rendered: String = "")
