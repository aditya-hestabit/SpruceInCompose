package com.example.spruceincompose.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerMenu(
    val id: Int,
    val icon: ImageVector,
    val title: String,
    val route: String,
    val modifier: Modifier = Modifier
)

