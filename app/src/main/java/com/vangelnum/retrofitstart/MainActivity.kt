package com.vangelnum.retrofitstart

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DestinationsNavHost(navGraph = NavGraphs.root)
        }
    }
}


fun getMovies(): List<Films> {
    var movie: List<Films> = listOf()
    runBlocking {
        launch {
            val response = ApiInterface.create().getMovies("popular")
            if (response.isSuccessful) {
                movie = response.body()!!
            }
        }
    }
    return movie
}


@Destination(start = true)
@Composable
fun MovieList(navigator: DestinationsNavigator) {

    val movie: List<Films> = getMovies()
    Log.d("check", movie.toString())
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
                            navigator.navigate(OpenDestination(currentUrl))
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

//            Row(modifier = Modifier
//                .height(10.dp)
//                .width(100.dp)
//                .background(Color.Red)) {
//
//            }
            //Text(text = "check", textAlign = TextAlign.End, modifier = Modifier.background(Color.Red))
//            Row(modifier = Modifier.fillMaxSize()) {
//                Text(text = "check", textAlign = TextAlign.End)
//            }

        }

    }
}