package com.vangelnum.retrofitstart

import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination
@Composable
fun Open(navigator: DestinationsNavigator, url: String) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Card {
            Image(
                painter = rememberAsyncImagePainter(model = url),
                contentDescription = "null",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
                    .clip(RoundedCornerShape(15.dp))
            )
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 100.dp, top = 15.dp, start = 15.dp, end = 15.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly)
                {
                    //0x95000000
                    Card(
                        backgroundColor = Color(0x95000000),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {

                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_outline_share_24),
                            contentDescription = "share",
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Card(
                        backgroundColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(70.dp)
                            .offset(0.dp,(-10).dp)
                            .clickable {

                            }
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_download_24),
                            contentDescription = "download",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(15.dp)
                        )
                    }
                    Card(
                        backgroundColor = Color(0x95000000),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {

                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_favorite_24_white),
                            contentDescription = "share",
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                }
            }

        }

    }
}
