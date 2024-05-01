package com.example.spruceincompose.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spruceincompose.utils.DataUtils
import com.example.spruceincompose.modals.PostModel

@Composable
fun PostsScreen(
    allPosts: List<PostModel> = DataUtils.allPosts,
    postDetail: (postmodel: PostModel) -> Unit
) {

    LazyColumn {
        items(allPosts.size) { item ->
            FeaturedCard(modifier = Modifier.padding(5.dp), postModel = allPosts[item]) {
                postDetail(it)
            }
        }
    }
}