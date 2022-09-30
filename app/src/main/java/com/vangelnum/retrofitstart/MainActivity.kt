package com.vangelnum.retrofitstart

import android.os.Bundle
import android.util.Log
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {

    val apiInterface = ApiInterface.create().getMovies(20)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apiInterface.enqueue(object : Callback<List<Films>> {
            override fun onResponse(call: Call<List<Films>>, response: Response<List<Films>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    setContent {
                        DestinationsNavHost(navGraph = NavGraphs.root)
                        //MovieList(movie = body!!)
                    }
                }
            }

            override fun onFailure(call: Call<List<Films>>, t: Throwable) {
                Log.d("tag", t.message.toString())
                Log.d("check2", t.toString())
            }
        })
    }
}

@Destination (start = true)
@Composable
fun MovieList(movie: List<Films>, navigator: DestinationsNavigator) {

    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(top = 5.dp, start = 5.dp,end = 5.dp),
        columns = GridCells.Fixed(2),
        content = {
            itemsIndexed(items = movie) { index, item ->
                MovieItem(movie = item)
            }
        }
    )

}

@Composable
fun MovieItem(movie: Films) {
    Column() {
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
            border = BorderStroke(1.dp,Color.Gray)
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