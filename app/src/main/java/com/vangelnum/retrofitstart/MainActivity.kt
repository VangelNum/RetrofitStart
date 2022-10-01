package com.vangelnum.retrofitstart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vangelnum.retrofitstart.filmsutils.Films
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
    var movie = listOf<Films>()

    val job = GlobalScope.launch(Dispatchers.IO) {
        val response = ApiInterface.create().getMovies(6)
        if (response.isSuccessful) {
            movie = response.body()!!
        }
    }
    runBlocking {
        job.join()
    }
    return movie

}


@Destination(start = true)
@Composable
fun MovieList(navigator: DestinationsNavigator) {

    val movie = getMovies()

    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(top = 5.dp, start = 5.dp, end = 5.dp),
        columns = GridCells.Fixed(2)
    ) {
        itemsIndexed(items = movie) { index, item ->
            MovieItem(movie = item)
        }
    }

}

@Composable
fun MovieItem(movie: Films) {
    Column {
        Image(
            painter = rememberAsyncImagePainter(model = movie.urls.full),
            contentDescription = "null",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .clickable {

                }
        )
        OutlinedButton(
            onClick = { /*TODO*/ },
            Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Text(
                text = "Скачать",
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 14.sp,
            )
        }
    }

}