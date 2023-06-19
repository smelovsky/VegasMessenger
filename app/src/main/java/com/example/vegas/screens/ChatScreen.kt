package com.example.vegas.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.vegas.MainActivity
import com.example.vegas.R
import com.example.vegas.db.MessageEntity
import com.example.vegas.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp


@Composable
fun ChatScreen(
    state: ChatScreenState,
) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(
                vertical = 8.dp,
                horizontal = 8.dp
            )
        ) {
            items(state.messages) { message ->
                ChatItem(message,
                    onItemClick = { id -> /*onItemClick(id) */ })
            }
        }
        if(state.isLoading)
            CircularProgressIndicator()
        if(state.error != null)
            Text(state.error)
    }
}

@Composable
fun ChatItem(item: MessageEntity,
             onItemClick: (id: Long) -> Unit) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onItemClick(item.id) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            MessageDetails(item.author, item.text, Modifier.weight(0.7f))

        }
    }
}


@Composable
fun MessageDetails(from: String,
                      msg: String,
                      modifier: Modifier,
                      horizontalAlignment: Alignment.Horizontal = Alignment.Start) {
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment) {
        Text(
            text = from,
            style = MaterialTheme.typography.h6
        )
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.medium
        ) {
            Text(
                text = msg,
                style = MaterialTheme.typography.body2
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainActivity.BottomBarChat(
) {

    var textFieldValue: TextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = viewModel.message
            )
        )
    }

    ConstraintLayout(

        modifier = Modifier.fillMaxWidth().height(50.dp)

    ) {

        val (contactIcon, textField, sendIcon ) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(contactIcon) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, margin = 0.dp)
                }
        ) {
            Row() {
                for(contact_index in 0..2) {
                    if (viewModel.contactListDetails[contact_index].show.value) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(viewModel.contactListDetails[contact_index].res_id),
                            tint = Color.Unspecified,
                            contentDescription = "contact_name",
                            modifier = Modifier
                                .scale(0.9F)
                                .clip(shape = RoundedCornerShape(10.dp))
                                .size(40.dp),
                        )
                    }
                }
            }
        }


        val keyboardController = LocalSoftwareKeyboardController.current

        TextField(
            value = textFieldValue,
            onValueChange = {
                newValue -> textFieldValue = newValue
                viewModel.message = textFieldValue.text
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            ),
            singleLine = true,
            //textStyle = TextStyle(fontSize = 16.sp ),
            textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(textField) {
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(contactIcon.end)
                    end.linkTo(sendIcon.start)
                },
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
        )

        IconButton(
            onClick = {
                keyboardController?.hide()

                var isReciverExist  = false
                viewModel.contactListDetails.forEach {
                    if (it.show.value) {
                        isReciverExist = true
                    }
                }
                if (isReciverExist) {
                    Log.d("zzz", "Send: ${textFieldValue.text}")
                    viewModel.addAndSendMessageToEveryOne(textFieldValue.text)

                    viewModel.message = ""
                    textFieldValue = TextFieldValue(text = "")
                } else {
                    toast(getApplicationContext().getResources().getString(R.string.no_one_to_send))
                }


            },
            modifier = Modifier
                .constrainAs(sendIcon) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end, margin = 4.dp)
                }

        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_send_24),
                contentDescription = "Send",
                tint = Color.Unspecified,
            )
        }
    }

}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}