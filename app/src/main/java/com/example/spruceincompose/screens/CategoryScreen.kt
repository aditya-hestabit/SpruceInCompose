package com.example.spruceincompose.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.spruceincompose.utils.DataUtils
import com.example.spruceincompose.modals.PostModel
import com.example.spruceincompose.ui.theme.fonts

@Composable
fun CategoryScreen(id: Int, title: String, postDetail: (postModel: PostModel) -> Unit) {

    val list = arrayListOf<PostModel>()
    for (post in DataUtils.allPosts) {
        if (post.categories.contains(id)) {
            list.add(post)

        }
    }

    LazyColumn {

        item {

            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontFamily = fonts,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp), color = Color.LightGray
            )
        }

        if (list.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "No posts available of this category",
                        fontFamily = fonts,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }
            }
        } else {

            items(count = list.size) { item ->
                FeaturedCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    postModel = list[item]
                ) {
                    postDetail(it)

                }
            }
        }
    }
}