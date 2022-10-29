package com.vangelnum.retrofitstart

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.vangelnum.retrofitstart.searchfilms.SearchItem
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
    per_page: Int,
): SearchItem {
    var movie = SearchItem(listOf(), 0, 0)
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

    val per_page by remember {
        mutableStateOf(30)
    }
    var page by remember {
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

    val order by remember {
        mutableStateOf("popular")
    }

    var text by remember {
        mutableStateOf("Популярные")
    }

    var searchtext by remember {
        mutableStateOf(false)
    }

    var query by remember {
        mutableStateOf("")
    }
    var movie: List<Films> = getMovies(per_page, page, status, order)
    val movie2: SearchItem = getSearch(status, query, per_page)


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
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }
        var visibleSearchBar by remember {
            mutableStateOf(false)
        }
        var visiblecurrentSearch by remember {
            mutableStateOf(true)
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    actions = {


                        LaunchedEffect(visibleSearchBar) {
                            if (visibleSearchBar) {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            }

                        }

                        val state = remember { mutableStateOf(TextFieldValue("")) }

                        AnimatedVisibility(visible = visiblecurrentSearch) {
                            IconButton(onClick = {
                                visiblecurrentSearch = false
                                visibleSearchBar = true
                                searchtext = true

                            }) {
                                Icon(painter = painterResource(id = R.drawable.ic_baseline_search_24),
                                    contentDescription = "search")
                            }

                        }
                        AnimatedVisibility(
                            visible = visibleSearchBar,

                            ) {
                            TextField(
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .fillMaxWidth()
                                    .padding(end = 10.dp)
                                    .scale(scaleX = 1F, scaleY = 0.9F),
                                value = state.value,
                                onValueChange = { value ->
                                    state.value = value
                                },
                                enabled = true,
                                shape = RoundedCornerShape(25.dp),

                                textStyle = TextStyle(color = Color.Black),
                                placeholder = {
                                    Text(
                                        text = "Search",
                                        fontSize = 14.sp,
                                    )
                                },
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (state.value.text != "")
                                            query = state.value.text
                                    }
                                ),
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_baseline_search_24),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            state.value = TextFieldValue("")

                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_round_close_24),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(20.dp)
                                        )
                                    }

                                },
                                singleLine = true,
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
                        }


                    },
                    title = {

                    },
                    navigationIcon = {
                        if (!searchtext) {
                            IconButton(onClick = {}) {
                                Icon(painter = painterResource(id = R.drawable.ic_baseline_menu_24),
                                    contentDescription = "menu")

                            }
                        } else {
                            IconButton(onClick = {
                                visibleSearchBar = false
                                visiblecurrentSearch = true
                                searchtext = false
                                keyboardController?.hide()
                            }) {
                                Icon(painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                                    contentDescription = "menu")

                            }
                        }
                    },
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    elevation = 3.dp
                )
            }
        ) {
            it.calculateTopPadding()
            if (!searchtext) {
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
                                        modifier = Modifier.padding(5.dp),
                                        painter = painterResource(id = R.drawable.random),
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
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f, fill = false)
                            .padding(start = 7.dp, top = 7.dp, end = 7.dp),
                        contentPadding = PaddingValues(bottom = 8.dp),
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
                        val itemsList = (0..99).toList()

                        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                            LazyRow(contentPadding = PaddingValues(5.dp)) {
                                items(itemsList) { index ->
                                    when (index + 1) {
                                        page -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(70.dp)
                                                    .clip(RoundedCornerShape(15.dp))
                                                    .background(Color.Black)
                                                    .border(2.dp,
                                                        Color.Yellow,
                                                        RoundedCornerShape(15.dp))
                                                    .clickable {
                                                        page = index + 1
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val cur = index + 1
                                                Text(
                                                    "$cur",
                                                    fontSize = 30.sp,
                                                    color = Color.White,
                                                    fontFamily = FontFamily(Font(R.font.jostregular))
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        index + 1 -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(70.dp)
                                                    .clip(RoundedCornerShape(15.dp))
                                                    .background(Color.Black)
                                                    .border(1.dp,
                                                        Color.Black,
                                                        RoundedCornerShape(15.dp))
                                                    .clickable {
                                                        coroutineScope.launch {
                                                            listState.animateScrollToItem(index = 0)
                                                            delay(300L)
                                                            page = index + 1
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val cur = index + 1
                                                Text(
                                                    "$cur",
                                                    color = Color.White,
                                                    fontSize = 30.sp,
                                                    fontFamily = FontFamily(Font(R.font.jostregular))
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }

                                    }

                                }
                            }
                        }

                    }
                }


            } else {
                LazyVerticalGrid(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.padding(start = 7.dp, top = 7.dp, end = 7.dp),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    columns = GridCells.Fixed(3)
                ) {
                    itemsIndexed(items = movie2.results) { index, list ->
                        Card(modifier = Modifier
                            .height(400.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(25.dp))) {
                            val painter = rememberAsyncImagePainter(model = list.urls.full)
                            val painterstate = painter.state
                            Image(
                                painter = painter,
                                contentDescription = "null",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        val currentUrl = list.urls.full
                                        val currentcolor = list.color
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
                                            text = list.likes.toString(),
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

}


