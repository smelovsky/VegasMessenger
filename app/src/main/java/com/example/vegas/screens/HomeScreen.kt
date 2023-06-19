package com.example.vegas.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.vegas.*
import com.example.vegas.R

@Composable
fun HomeScreen() {
    Column() {
        Image(
            painterResource(R.drawable.vegas_11),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun MainActivity.BottomBarHome() {

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {

        androidx.compose.material3.Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.addAndSendMessageToEveryOne("Alarm", false)
            }
        ) {
            androidx.compose.material3.Text(text = stringResource(R.string.alarm))
        }
    }

}
