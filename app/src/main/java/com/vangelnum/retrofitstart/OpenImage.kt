package com.vangelnum.retrofitstart

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


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
                            .offset(0.dp, (-10).dp)
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
                        val context = LocalContext.current
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_favorite_24_white),
                            contentDescription = "share",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                   GlobalScope.launch {

                                        var url2 = URL(url)
                                        var connection: HttpURLConnection? = null
                                        connection = url2.openConnection() as HttpURLConnection?
                                        connection!!.connect()

                                        var inputStream: InputStream? = null
                                        inputStream = connection.inputStream
                                        val myBitmap = BitmapFactory.decodeStream(inputStream)
                                        val share = Intent(Intent.ACTION_SEND)
                                        share.type = "image/*"
                                        share.type = "text/html"
                                        share.putExtra(Intent.EXTRA_TEXT, "Text message here")
                                        val bytes = ByteArrayOutputStream()
                                        myBitmap.compress(Bitmap.CompressFormat.JPEG,
                                            100,
                                            bytes)
                                        val path = MediaStore.Images.Media.insertImage(
                                            context.contentResolver,
                                            myBitmap,
                                            "Title",
                                            null
                                        )
                                        val imageUri = Uri.parse(path)
                                        share.putExtra(Intent.EXTRA_STREAM, imageUri)
                                        context.startActivity(Intent.createChooser(share,
                                            "Select"))
                                    }

                                }
                        )
                    }

                }
            }

        }

    }
}
