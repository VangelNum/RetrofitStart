package com.vangelnum.retrofitstart

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.rememberAsyncImagePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


@Destination
@Composable
fun Open(navigator: DestinationsNavigator, url: String, color: String) {

    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    Log.d("color", color)
    var paddingSizeImage by remember {
        mutableStateOf(15.dp)
    }
    var visible by remember {
        mutableStateOf(true)
    }
    var count by remember {
        mutableStateOf(1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(color.toColorInt())
            )
    ) {
        Card(modifier = Modifier
            .clickable {
                count++
                if (count % 2 == 0) {
                    paddingSizeImage = 0.dp
                    visible = false
                } else {
                    paddingSizeImage = 15.dp
                    visible = true
                }
            }
            .fillMaxSize()
            .padding(paddingSizeImage)
            .clip(RoundedCornerShape(paddingSizeImage))
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = url),
                contentDescription = "null",
                contentScale = ContentScale.Crop,
            )
            AnimatedVisibility(
                visible = visible,
                exit = fadeOut(),
                enter = fadeIn()
            ) {

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
                                    share(context = context,url = url, coroutine = coroutine)
                                }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_outline_share_24),
                                contentDescription = "share",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(10.dp)

                            )
                        }
                        Card(
                            backgroundColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(70.dp)
                                .offset(0.dp, (-10).dp)
                                .clickable {
                                    download(url,context)
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
                                modifier = Modifier
                                    .padding(10.dp)
                            )
                        }

                    }
                }

            }
        }

    }
}

private fun share(coroutine: CoroutineScope,context: Context,url: String) {
    val job = coroutine.launch(Dispatchers.IO) {

        val url2 = URL(url)
        val connection: HttpURLConnection? =
            url2.openConnection() as HttpURLConnection?
        connection!!.connect()

        val inputStream: InputStream? = connection.inputStream
        val myBitmap = BitmapFactory.decodeStream(inputStream)
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/*"
        share.type = "text/*"
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
    runBlocking {
        job.join()
    }
}

private fun download(url: String, context: Context) {
    val request = DownloadManager.Request(Uri.parse(url))
    request.setDescription("Downloading")
    request.setMimeType("image/*")
    request.setTitle("File")
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
        "photo.png")
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
    manager!!.enqueue(request)
}
