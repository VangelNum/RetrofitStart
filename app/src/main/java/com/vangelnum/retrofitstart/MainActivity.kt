package com.vangelnum.retrofitstart

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
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
import com.vangelnum.retrofitstart.searchfilms.Result
import com.vangelnum.retrofitstart.searchfilms.SearchItem
import com.vangelnum.retrofitstart.searchfilms.Urls
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
    status: ConnectivityObserver.Status,
    order: String,
): List<Films> {
    var movie: List<Films> = listOf()
    if (status.toString() == "Available") {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val response = ApiInterface.create().getMovies(order, per_page, page)
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

@OptIn(DelicateCoroutinesApi::class)
private fun getMovies2(
    status: ConnectivityObserver.Status,
    count: Int,
): List<Films> {
    var movie: List<Films> = listOf()
    if (status.toString() == "Available") {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val response = ApiInterface.create().getMovies2(count)
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

@OptIn(DelicateCoroutinesApi::class)
private fun getSearch(
    status: ConnectivityObserver.Status,
    query: String,
    per_page: Int
): SearchItem {
    var movie: SearchItem = SearchItem(listOf(),0,0)
    if (status.toString() == "Available") {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val response = ApiInterface.create().getSearch(query, per_page)
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





@OptIn(ExperimentalComposeUiApi::class)
@Destination(start = true)
@Composable
fun MovieList(navigator: DestinationsNavigator) {

    var per_page by remember {
        mutableStateOf(30)
    }
    var page by remember {
        mutableStateOf(1)
    }

    var whatscurrentpage by remember {
        mutableStateOf(1)
    }
    var backgroundColor1 by remember {
        mutableStateOf(Color.White)
    }
    var newcolor1 by remember {
        mutableStateOf(Color(0xFF6F8BF0))
    }
    var backgroundColor2 by remember {
        mutableStateOf(Color.White)
    }
    var newcolor2 by remember {
        mutableStateOf(Color(0xFF6F8BF0))
    }
    var backgroundColor3 by remember {
        mutableStateOf(Color.White)
    }
    var newcolor3 by remember {
        mutableStateOf(Color(0xFF6F8BF0))
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

    var order by remember {
        mutableStateOf("popular")
    }

    var text by remember {
        mutableStateOf("Популярные")
    }

    var movie: List<Films> = getMovies(per_page, page, status, order)
    var movie2: SearchItem = getSearch(status,"apple",10)
    Log.d("movie",movie2.results.toString())
    //var movie2: Result = getSearch(status,"green",10)

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
                        val state = remember { mutableStateOf(TextFieldValue("")) }
                        val keyboardController = LocalSoftwareKeyboardController.current
                        TextField(
                            value = state.value,
                            onValueChange = { value ->
                                state.value = value
                            },
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp)
                                .scale(scaleX = 1F, scaleY = 0.9F),
                            textStyle = TextStyle(color = Color.Black),
                            placeholder = {
                                Text(
                                    text = "Search",
                                    fontSize = 14.sp,
                                )
                            },
                            keyboardActions = KeyboardActions(
                               onDone = {
                                   Log.d("check",state.value.text)
                                   //movie2 = getSearch(status, state.value.text,10 )
                               }
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            },
                            trailingIcon = {
                                if (state.value != TextFieldValue("")) {
                                    IconButton(
                                        onClick = {
                                            state.value =
                                                TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(20.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            // The TextFiled has rounded corners top left and right by default
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black,
                                cursorColor = Color.Black,
                                leadingIconColor = Color.Black,
                                trailingIconColor = Color.Black,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Black
                            )
                        )


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
            Column {
                Text(
                    fontFamily = FontFamily(Font(R.font.ubuntulight)),
                    text = text,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 10.dp, top = 15.dp)
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
                            backgroundColor = newcolor1
                        ) {
                            IconButton(onClick = {
                                movie = getMovies(per_page, page, status, "popular")
                                whatscurrentpage = 1
                                text = "Популярные"
                                newcolor1 = Color(0xFF6F8BF0)
                                backgroundColor1 = Color.White
                                newcolor2 = Color(0xFF6F8BF0)
                                backgroundColor2 = Color.White
                                newcolor3 = Color(0xFF6F8BF0)
                                backgroundColor3 = Color.White
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_local_fire_department_24),
                                    contentDescription = "null",
                                    tint = backgroundColor1
                                )
                            }

                        }
                        Card(
                            modifier = Modifier
                                .height(40.dp)
                                .width(70.dp)
                                .clip(RoundedCornerShape(25.dp)),
                            backgroundColor = backgroundColor2
                        ) {
                            IconButton(onClick = {
                                movie = getMovies(per_page, page, status, "latest")
                                whatscurrentpage = 2
                                text = "Последние"
                                newcolor1 = Color.White
                                backgroundColor1 = Color(0xFF6F8BF0)
                                newcolor2 = Color.White
                                backgroundColor2 = Color(0xFF6F8BF0)
                                newcolor3 = Color(0xFF6F8BF0)
                                backgroundColor3 = Color.White
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_access_time_24),
                                    contentDescription = "null",
                                    tint = newcolor2
                                )
                            }

                        }
                        Card(
                            modifier = Modifier
                                .height(40.dp)
                                .width(70.dp)
                                .clip(RoundedCornerShape(25.dp)),
                            backgroundColor = backgroundColor3
                        ) {
                            IconButton(onClick = {
                                whatscurrentpage = 3
                                text = "Случайные"
                                movie = getMovies2(status, 30)
                                newcolor1 = Color.White
                                backgroundColor1 = Color(0xFF6F8BF0)
                                newcolor2 = Color(0xFF6F8BF0)
                                backgroundColor2 = Color.White
                                newcolor3 = Color.White
                                backgroundColor3 = Color(0xFF6F8BF0)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_favorite_24_white),
                                    contentDescription = "null",
                                    tint = newcolor3
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
    }
    if (status.toString() == "Available") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 15.dp, end = 15.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Card(modifier = Modifier.size(50.dp),
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
                        modifier = Modifier.size(30.dp),
                        tint = Color.Black
                    )
                }


            }
        }

    }

}


