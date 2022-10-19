package com.vangelnum.retrofitstart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
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
fun getMovies(context: Context, status: ConnectivityObserver.Status): List<Films> {
    var movie: List<Films> = listOf()
    if (status.toString() == "Available") {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val response = ApiInterface.create().getMovies("popular")
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

@Destination(start = true)
@Composable
fun MovieList(navigator: DestinationsNavigator) {
    val context = LocalContext.current

    val status by connectivityObserver.observe().collectAsState(
        initial = ConnectivityObserver.Status.Unavailable
    )

    var color by remember {
        mutableStateOf(Color.Green)
    }

    val movie: List<Films> = getMovies(context, status)

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
        LazyVerticalGrid(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(all = 5.dp),
            columns = GridCells.Fixed(2)
        ) {
            itemsIndexed(items = movie) { _, movie ->

                Card(modifier = Modifier
                    .height(400.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25.dp))) {
                    SubcomposeAsyncImage(
                        model = movie.urls.full,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                val currentUrl = movie.urls.full
                                val currentcolor = movie.color
                                navigator.navigate(OpenDestination(currentUrl, currentcolor))
                            }
                    ) {
                        val state = painter.state
                        if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                            Box(modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    color = Color.Green
                                )
                            }
                        } else {
                            SubcomposeAsyncImageContent()
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Card(
                            shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp),
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