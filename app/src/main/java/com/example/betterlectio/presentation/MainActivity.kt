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
    var lektier = ""
    var base64Cookie = ""
    var responseCookie = "Loading..."
    var responseLektier = "loading..."
    runBlocking {
        // run async code here
        val getCookie = async {
            // async code here
            getResponseString("auth", base64Cookie)
        }
        // use the result of the async operation
        responseCookie = getCookie.await()
        base64Cookie = responseCookie
    }
    runBlocking {
        val getLektier = async {
            // async code here
            getResponseString("lektier",base64Cookie)
        }

        responseLektier = getLektier.await()
        lektier = responseLektier
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = lektier
        )
    }
}

suspend fun getResponseString(endPoint: String, authCookie: String): String = withContext(Dispatchers.IO) {
    val url = URL("https://api.betterlectio.dk/$endPoint")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    if (endPoint == "auth") {
        connection.addRequestProperty("brugernavn", "XXXXXXXXXX")
        connection.addRequestProperty("adgangskode", "XXXXXXXXXX")
        connection.addRequestProperty("skole_id", "681")
    } else if (endPoint == "skema") {
        connection.addRequestProperty("uge", "1")
        connection.addRequestProperty("\u00E5r", "2023") // not working
        connection.addRequestProperty("cookie", authCookie)
    } else {
        connection.addRequestProperty("nonce", "no ide") // no idea
        connection.addRequestProperty("cookie", authCookie)
    }

    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val inputStream = connection.getHeaderField("set-lectio-cookie")
        inputStream
    } else {
        throw IOException("HTTP error code: $responseCode")
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}