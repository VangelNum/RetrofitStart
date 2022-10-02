package com.vangelnum.retrofitstart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination
@Composable
fun Open(navigator: DestinationsNavigator, url: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card() {
            Image(
                painter = rememberAsyncImagePainter(model = url),
                contentDescription = "null",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                OutlinedButton(
                    onClick = { /*TODO*/ },
                    Modifier
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

    }
}
