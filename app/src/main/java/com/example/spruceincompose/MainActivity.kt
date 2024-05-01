package com.example.spruceincompose

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.PermMedia
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spruceincompose.screens.CategoryScreen
import com.example.spruceincompose.screens.HomeScreen
import com.example.spruceincompose.screens.PostDetailScreen
import com.example.spruceincompose.screens.PostsScreen
import com.example.spruceincompose.ui.theme.SpruceInComposeTheme
import com.example.spruceincompose.ui.theme.ThemeColor
import com.example.spruceincompose.ui.theme.fonts
import com.example.spruceincompose.utils.DataUtils
import com.example.spruceincompose.utils.DrawerMenu
import com.example.spruceincompose.utils.NavigationRoutes
import com.example.spruceincompose.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpruceInComposeTheme(darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationSetup()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG", "onDestroy: ")
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun NavigationSetup(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
) {

    val viewModel: SpruceViewModel = hiltViewModel()
    val categories = viewModel.categoryFlow.collectAsState()

    val menus by remember {
        mutableStateOf(
            arrayListOf(
                DrawerMenu(101, Icons.Rounded.Home, "Home", NavigationRoutes.Home.name),
                DrawerMenu(101, Icons.Rounded.PermMedia, "Posts", NavigationRoutes.Post.name),
                DrawerMenu(101, Icons.Rounded.Category, "Category", NavigationRoutes.Category.name)
            )
        )
    }

    categories.value?.let {
        when (it) {
            is Resource.Failure -> {
                Log.d(
                    "categoryResponse",
                    "NavigationSetup: CategoryFailure - ${it.exception.message}"
                )
            }

            Resource.Loading -> {
                // Do Nothing
            }

            is Resource.Success -> {
                Log.d("categoryResponse", "NavigationSetup: CategorySuccess - ${it.result}")
                val tempList = it.result

                for (categoryModel in tempList) {
                    menus.add(
                        DrawerMenu(
                            categoryModel.id,
                            DataUtils.findIcon(categoryModel.slug),
                            categoryModel.name,
                            NavigationRoutes.Category.name,
                            Modifier.padding(start = 20.dp)
                        )
                    )
                }
            }
        }
    }

    var icon: ImageVector by remember {
        mutableStateOf(menus[0].icon)
    }



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(menus = menus) { route, itIcon, id, title ->
                    scope.launch {
                        drawerState.close()
                    }
                    icon = itIcon
                    if ((route != NavigationRoutes.Category.name && id == 101) || (route == NavigationRoutes.Category.name && id != 101))
                        navController.navigate(route = if (route == NavigationRoutes.Category.name) "${route}/$id/$title" else route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                }
            }
        },
    ) {

        val lazyListState: LazyListState = rememberLazyListState()

        Scaffold(topBar = {
            CustomTopAppBar(menuIcon = {
                Icon(imageVector = Icons.Rounded.Menu, contentDescription = "Side Drawer Icon")
            }, lazyListState) {
                scope.launch {
                    drawerState.apply {
                        if (isClosed) open() else close()
                    }
                }
            }
        }) {

            NavHost(
                navController = navController,
                startDestination = NavigationRoutes.Home.name,
                modifier = Modifier.padding(it)
            ) {

                composable(route = NavigationRoutes.Home.name) {
                    HomeScreen(lazyListState = lazyListState) { postData ->
                        DataUtils.postModel = postData
                        navController.navigate(NavigationRoutes.PostDetails.name)
                    }
                }

                composable(route = NavigationRoutes.Post.name) {
                    PostsScreen { postData ->
                        DataUtils.postModel = postData
                        navController.navigate(NavigationRoutes.PostDetails.name)
                    }
                }

                composable(route = "${NavigationRoutes.Category.name}/{id}/{title}") { navBackStackEntry ->
                    val catId = navBackStackEntry.arguments?.getString("id")!!
                    val catTitle = navBackStackEntry.arguments?.getString("title")!!

                    CategoryScreen(catId.toInt(), title = catTitle) { postData ->
                        DataUtils.postModel = postData
                        navController.navigate(NavigationRoutes.PostDetails.name)
                    }
                }

                composable(route = NavigationRoutes.PostDetails.name) {
                    PostDetailScreen(
                        postModel = DataUtils.postModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    menuIcon: @Composable () -> Unit,
    lazyListState: LazyListState,
    onMenuClicked: () -> Unit
) {
    val size by animateIntAsState(
        targetValue = if (0 != lazyListState.firstVisibleItemIndex) {
            30
        } else {
            40
        },
        label = "dpAnimation",
    )

    CenterAlignedTopAppBar(
        title = {
            Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Spruce",
                    fontFamily = fonts,
                    fontWeight = FontWeight.Black,
                    fontSize = size.sp
                )
            }
        },

        navigationIcon = {
            IconButton(onClick = {
                onMenuClicked()
            }) {
                menuIcon()
            }
        }

    )
}

@Composable
private fun DrawerContent(
    menus: ArrayList<DrawerMenu>,
    onMenuClick: (String, ImageVector, Int, String) -> Unit
) {
    var selected by remember {
        mutableIntStateOf(0)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(menus.size) {

                NavigationDrawerItem(
                    label = { Text(text = menus[it].title) },
                    icon = {
                        Icon(
                            imageVector = menus[it].icon,
                            contentDescription = null,
                            modifier = menus[it].modifier,
                            tint = if (menus[it].id != 101) ThemeColor else Color.Gray
                        )
                    },
                    selected = menus.indexOf(menus[it]) == selected,
                    onClick = {
                        onMenuClick(menus[it].route, menus[it].icon, menus[it].id, menus[it].title)
                        selected = menus.indexOf(menus[it])
                    }
                )

                if (menus.indexOf(menus[it]) < 3)
                    Divider(thickness = 1.dp)
            }

        }
    }
}