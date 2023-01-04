package com.example.betterlectio.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import java.net.URL
import java.net.HttpURLConnection
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import androidx.wear.compose.material.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    var result = "Loading..."
    runBlocking {
        // run async code here
        val deferredResult = async {
            // async code here
            getResponseString()
        }
        // use the result of the async operation
        result = deferredResult.await()

    }
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        text = result
    )
}

suspend fun getResponseString(): String = withContext(Dispatchers.IO) {
    val url = URL("https://raw.githubusercontent.com/BetterLectio/WearOS-Native/main/README.md")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val inputStream = connection.inputStream
        inputStream.bufferedReader().readText()
    } else {
        throw IOException("HTTP error code: $responseCode")
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}