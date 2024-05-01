package com.example.spruceincompose.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.spruceincompose.utils.DataUtils
import com.example.spruceincompose.R
import com.example.spruceincompose.utils.Resource
import com.example.spruceincompose.SpruceViewModel
import com.example.spruceincompose.modals.PostModel
import com.example.spruceincompose.ui.theme.BottomCardBrush
import com.example.spruceincompose.ui.theme.GradientCardBrush
import com.example.spruceincompose.ui.theme.ThemeColor
import com.example.spruceincompose.ui.theme.fonts

@SuppressLint("MutableCollectionMutableState")
@Composable
fun HomeScreen(
    lazyListState: LazyListState = rememberLazyListState(),
    postDetails: (postModel: PostModel) -> Unit
) {

    val viewModel: SpruceViewModel = hiltViewModel()
    val postResult = viewModel.postDataFlow.collectAsState()

    val postList by rememberSaveable {
        mutableStateOf(arrayListOf<PostModel>())
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var isPageLoading by remember {
        mutableStateOf(false)
    }

    var initState by remember {
        mutableStateOf(true)
    }

    postResult.value?.let {
        when (it) {
            is Resource.Failure -> {
                isLoading = false
                isPageLoading = false

                if (postList.isNotEmpty()) viewModel.isLastPageReached = true

                Log.d("PostResponse", "HomeScreen: postFailure - ${it.exception.message}")
            }

            Resource.Loading -> {
                if (viewModel.mPage == 1)
                    isLoading = true
                else isPageLoading = true
            }

            is Resource.Success -> {
                isLoading = false
                isPageLoading = false

                DataUtils.allPosts = it.result

                if (!viewModel.isLastPageReached) {
                    if (initState && postList.isEmpty()) {
                        postList.addAll(it.result)
                        initState = false
                    }
                    Log.d("HomeResponse", "HomeScreen: postListSize - ${postList.size}")
                } else {
                    viewModel.isLastPageReached = true
                }
            }
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (isLoading) {
            item {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
        } else {
            items(postList.size) {
                if (it < 3) {
                    TopCard(modifier = Modifier, postModel = postList[it])
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "FEATURED POST",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        fontFamily = fonts
                    )

                    Divider(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(.2f)
                            .clip(shape = CircleShape),
                        thickness = 3.dp,
                        color = ThemeColor
                    )
                }
            }

            items(postList) {
                FeaturedCard(modifier = Modifier, postModel = it) { postDetails(it) }
            }

            item {

                val context = LocalContext.current

                LaunchedEffect(key1 = Unit) {
                    Toast.makeText(context, "No More Content", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopCard(modifier: Modifier = Modifier, postModel: PostModel) {

    var categories by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        val temp = StringBuilder()
        for (it in postModel.cateResult.indices) {
            temp.append("${postModel.cateResult[it].name}${if (it < postModel.cateResult.size - 1) ", " else ""}")
        }
        categories = temp.toString()
    }

    ElevatedCard(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .padding(vertical = 5.dp)
            .aspectRatio(16 / 11f)
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Box {
            AsyncImage(
                model = postModel.imageResult.guid.rendered,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(brush = GradientCardBrush())
                    .padding(5.dp)
            ) {

                Text(
                    text = "Categories : $categories",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = postModel.title.rendered,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.ExtraBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AsyncImage(
                        model = "",
                        contentDescription = "Author Image",
                        error = painterResource(
                            id = R.drawable.person_image_place_holder
                        ),
                        colorFilter = ColorFilter.tint(color = Color.White),
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .size(24.dp)
                            .background(color = Color.LightGray, shape = CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = "Hesta-Admin",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fonts,
                        color = Color.White
                    )

                    Row (modifier = Modifier.padding(start = 10.dp)) {

                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = "Callender Icon",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = DataUtils.dateFormat(postModel.date),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = fonts
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "share icon",
                            tint = Color.White,
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(5.dp))
                                .border(
                                    width = 1.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(5.dp)
                                .size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturedCard(
    modifier: Modifier = Modifier,
    postModel: PostModel,
    postDetails: (postModel: PostModel) -> Unit
) {

    var categories by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        val temp = StringBuilder()
        for (it in postModel.cateResult.indices) {
            temp.append("${postModel.cateResult[it].name}${if (it < postModel.cateResult.size - 1) ", " else ""}")
        }
        categories = temp.toString()
    }

    ElevatedCard(
        onClick = { postDetails(postModel) },
        modifier = modifier
            .padding(vertical = 5.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {

        Column {
            Box(contentAlignment = Alignment.BottomStart) {
                AsyncImage(
                    model = postModel.imageResult.guid.rendered,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16 / 11f)
                )

                LazyRow(
                    modifier = Modifier
                        .background(brush = BottomCardBrush())
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) {
                        Text(
                            text = postModel.tagsResult[it].name,
                            modifier = Modifier
                                .padding(
                                    start = if (it == 0) 12.dp else 0.dp,
                                    end = if (it == 4) 12.dp else 0.dp
                                )
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(7.dp)
                                )
                                .padding(horizontal = 17.dp, vertical = 7.dp),
                            fontSize = 13.sp,
                            fontFamily = fonts,
                            fontWeight = FontWeight.Light,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {

                Text(
                    text = postModel.title.rendered,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 19.sp,
                    fontFamily = fonts,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Categories :",
                        fontFamily = fonts,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = categories,
                        fontSize = 11.sp,
                        maxLines = 1,
                        fontFamily = fonts,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 3.dp),
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .padding(vertical = 20.dp),
                    color = ThemeColor
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = "Callender Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = DataUtils.dateFormat(postModel.date),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fonts,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = postModel.content.rendered.trim(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    fontSize = 15.sp,
                    fontFamily = fonts,
                    lineHeight = TextUnit(1.5f, TextUnitType.Em)
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AsyncImage(
                        model = "",
                        contentDescription = "Author Image",
                        error = painterResource(
                            id = R.drawable.person_image_place_holder
                        ),
                        colorFilter = ColorFilter.tint(color = Color.White),
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .size(25.dp)
                            .background(color = Color.LightGray, shape = CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = "Hesta-Admin",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fonts
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = { postDetails(postModel) }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "share icon",
                            tint = ThemeColor,
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(5.dp))
                                .border(
                                    width = 1.dp,
                                    color = ThemeColor,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(5.dp)
                                .size(15.dp)
                        )
                    }

                    TextButton(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(containerColor = ThemeColor),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(vertical = 5.dp, horizontal = 15.dp)
                    ) {
                        Text(
                            text = "Read More",
                            fontSize = 13.sp,
                            fontFamily = fonts,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

