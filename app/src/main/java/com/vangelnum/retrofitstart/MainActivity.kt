package com.vangelnum.retrofitstart

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vangelnum.retrofitstart.destinations.OpenDestination
import com.vangelnum.retrofitstart.filmsutils.Films
import kotlinx.coroutines.*

private lateinit var connectivityObserver: ConnectivityObserver

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityObserver = NetworkConnectivityObserver(context = this)

        setContent {
            DestinationsNavHost(navGraph = NavGraphs.root)
        }
    }
}


@OptIn(DelicateCoroutinesApi::class)
private fun getMovies(
    per_page: Int,
    page: Int,
    context: Context,
    status: ConnectivityObserver.Status,
): List<Films> {
    var movie: List<Films> = listOf()
    if (status.toString() == "Available") {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val response = ApiInterface.create().getMovies("popular", per_page, page)
            if (response.isSuccessful) {
                movie = response.body()!!
            } else {
                //error
            }
        }
        runBlocking {
            job.join()
        }
    } else {
        //error
    }
    return movie
}

@SuppressLint("CoroutineCreationDuringComposition")
@Destination(start = true)
@Composable
fun MovieList(navigator: DestinationsNavigator) {

    var per_page by remember {
        mutableStateOf(30)
    }
    var page by remember {
        mutableStateOf(1)
    }

    var visible by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    val listState = rememberLazyGridState()

    val context = LocalContext.current

    val status by connectivityObserver.observe().collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )

    var color by remember {
        mutableStateOf(Color.Green)
    }
    val movie: List<Films> = getMovies(per_page, page, context, status)

    if (movie.isEmpty()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = color
                )
                Text(
                    modifier = Modifier.padding(top = 15.dp),
                    text = "Проверка подключения к интернету",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )
            }
            LaunchedEffect(key1 = movie) {
                delay(5000L)
                color = Color.Red
                Toast.makeText(context, "Ошибка подключения", Toast.LENGTH_LONG).show()
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            OutlinedButton(
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.padding(bottom = 20.dp),
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
            ) {
                Text(text = "Открыть настройки подключения", color = Color.Black)
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(painter = painterResource(id = R.drawable.ic_baseline_search_24),
                                contentDescription = "search")
                        }
                    },
                    title = {

                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(painter = painterResource(id = R.drawable.ic_baseline_menu_24),
                                contentDescription = "menu")
                        }
                    },
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    elevation = 3.dp
                )
            }
        ) {
            it.calculateTopPadding()
            Column() {
                Text(
                    fontFamily = FontFamily(Font(R.font.ubuntulight)),
                    text = "Популярные",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 10.dp,top = 15.dp)
                )
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(50.dp)
                    .background(Color(0xAD87A8FD), shape = RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .height(40.dp)
                                .width(70.dp)
                                .clip(RoundedCornerShape(25.dp)),
                            backgroundColor = Color(0xFF6F8BF0)
                        ) {
                            IconButton(onClick = {}) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_search_24),
                                    contentDescription = "null",
                                    tint = Color.White
                                )
                            }

                        }
                        Card(
                            modifier = Modifier
                                .height(40.dp)
                                .width(70.dp)
                                .clip(RoundedCornerShape(25.dp)),
                            backgroundColor = Color(0xFF6F8BF0)
                        ) {
                            IconButton(onClick = {}) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_search_24),
                                    contentDescription = "null",
                                    tint = Color.White
                                )
                            }

                        }
                        Card(
                            modifier = Modifier
                                .height(40.dp)
                                .width(70.dp)
                                .clip(RoundedCornerShape(25.dp)),
                            backgroundColor = Color(0xFF6F8BF0)
                        ) {
                            IconButton(onClick = {}) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_search_24),
                                    contentDescription = "null",
                                    tint = Color.White
                                )
                            }

                        }

                    }
                }

                LazyVerticalGrid(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.padding(start = 7.dp, top = 7.dp, end = 7.dp),
                    contentPadding = PaddingValues(bottom = 10.dp),
                    columns = GridCells.Fixed(3)
                ) {
                    itemsIndexed(items = movie) { index, movie ->
                        if (index == 29) {
                            visible = true
                        }
                        Card(modifier = Modifier
                            .height(400.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(25.dp))) {
                            val painter = rememberAsyncImagePainter(model = movie.urls.full)
                            val painterstate = painter.state
                            Image(
                                painter = painter,
                                contentDescription = "null",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        val currentUrl = movie.urls.full
                                        val currentcolor = movie.color
                                        navigator.navigate(OpenDestination(currentUrl,
                                            currentcolor))
                                    }
                            )
                            if (painterstate is AsyncImagePainter.State.Loading) {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        color = Color.Green
                                    )
                                }

                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Card(
                                    shape = RoundedCornerShape(topStart = 15.dp,
                                        bottomStart = 15.dp),
                                    backgroundColor = Color.Black
                                ) {
                                    Row(modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_baseline_remove_red_eye_24),
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = movie.likes.toString(),
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }


                            }
                        }


                    }

                }


            }
        }
        if (visible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp, end = 10.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Card(modifier = Modifier.size(70.dp),
                    shape = CircleShape,
                    backgroundColor = Color.White,
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(index = 0)
                            delay(300L)
                            page++
                        }
                    }) {
                        Icon(painter = painterResource(id = R.drawable.ic_round_refresh_24),
                            contentDescription = "arrow",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Black
                        )
                    }


                }
            }
        }
    }

}

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit,
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}


