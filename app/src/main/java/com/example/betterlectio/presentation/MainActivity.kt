package com.example.betterlectio.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import java.net.URL
import java.net.HttpURLConnection
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.*
import org.json.JSONObject
import org.json.*
import org.json.JSONArray

open class JSONArray

open class JSONObject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val s = load()
        setContent {
            WearApp(s)
        }
    }
}

fun load(): String {
    var skema = ""
    var forside = ""
    var base64Cookie = ""
    var responseCookie = "Loading..."
    var responseForside = "loading..."
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
    return runBlocking {
        val getForside = async {
            // async code here
            println(base64Cookie)
            getResponseString("forside", base64Cookie)
        }

        responseForside = getForside.await()
        forside = responseForside
        skema = (JSONObject(forside).get("skema") as JSONArray).toString()
        println(skema)
        println(forside)
        skema
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun WearApp(skema: String) {
    val listState = rememberScalingLazyListState()
    Scaffold(
        timeText = {
            if (!listState.isScrollInProgress) {
                TimeText()
            }
        },
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = listState
            )
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            anchorType = ScalingLazyListAnchorType.ItemCenter
        ) {
            item {
                ListHeader {
                    Text(text = "Næste moduler")
                }
            }
            for (i in 0..5) {
                val skemaJson = JSONArray(skema)
                // get item (the index is i)
                val item = skemaJson.getJSONObject(i)
                // get the value of the key 
                val hold = item.getString("hold")
                val tidspunkt = item.getString("tidspunkt")
                val status = item.getString("status")
                val lokale = item.getString("lokale")

                println(hold+lokale+tidspunkt+status)
                item { 
                    TitleCard(
                        onClick = { },
                        title = { Text("$hold") },
                    ) {
                        Text("$lokale")
                        Text("$tidspunkt")
                    }
                }
            }
        }
    }

}

suspend fun getResponseString(endPoint: String, authCookie: String): String = withContext(Dispatchers.IO) {
    val url = URL("https://api.betterlectio.dk/$endPoint")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    if (endPoint == "auth") {
        connection.addRequestProperty("brugernavn", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
        connection.addRequestProperty("adgangskode", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
        connection.addRequestProperty("skole_id", "681")
    } else if (endPoint == "skema") {
        connection.addRequestProperty("uge", "1")
        connection.addRequestProperty("\u00E5r", "2023") // not working
        connection.addRequestProperty("lectio-cookie", authCookie)
    } else {
        connection.addRequestProperty("lectio-cookie", authCookie)
    }

    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        if (endPoint == "auth" ) {
            val inputStream = connection.getHeaderField("set-lectio-cookie")
            inputStream
        } else {
            val inputStream = connection.inputStream
            inputStream.bufferedReader().readText()
        }
    } else {
        throw IOException("HTTP error code: $responseCode")
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(load().toString())
}