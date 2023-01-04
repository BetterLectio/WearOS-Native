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
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.betterlectio.presentation.theme.BetterLectioTheme

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
    @OptIn(ExperimentalPagerApi::class)
@Composable
fun VerticalPagerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        val items = createItems()
        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()

        Row(
            modifier = Modifier.weight(1f)
        ) {
            VerticalPager(
                count = items.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { currentPage ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = items[currentPage].title,
                        style = MaterialTheme.typography.h2
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = items[currentPage].subtitle,
                        style = MaterialTheme.typography.h4
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = items[currentPage].description,
                        style = MaterialTheme.typography.body1
                    )
                }
            }

            VerticalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp),
            )
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(page = 2)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Scroll to the third page")
        }
    }
}
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}
