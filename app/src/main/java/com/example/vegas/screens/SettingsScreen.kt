package com.example.vegas.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vegas.AppFunction
import com.example.vegas.MainActivity
import com.example.vegas.R
import com.example.vegas.viewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun SettingsScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        val theme = listOf(stringResource(R.string.light), stringResource(R.string.dark))

        val (selectedThemeOption, onThemeOptionSelected) = remember { mutableStateOf(viewModel.currentTheme) }
        val checkedStateExitFromApp = remember { mutableStateOf(viewModel.askToExitFromApp) }

        val checkedStateContacts = listOf(
            remember { mutableStateOf(viewModel.contactLorik) },
            remember { mutableStateOf(viewModel.contactAlyona) },
            remember { mutableStateOf(viewModel.contactNastik) }
        )

        Column(
            modifier = Modifier.padding(0.dp, 16.dp),
        ) {

            androidx.compose.material3.Text(
                text = "${stringResource(R.string.theme)}:",
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Row(
                Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .height(56.dp), verticalAlignment = Alignment.CenterVertically) {

                for(theme_index in 0..theme.lastIndex) {
                    androidx.compose.material3.RadioButton(
                        selected = (theme_index == selectedThemeOption),
                        onClick = {
                            onThemeOptionSelected(theme_index)
                            viewModel.currentTheme = theme_index
                            AppFunction.putPreferences.run()
                        },
                        colors = androidx.compose.material3.RadioButtonDefaults.colors(),

                        )

                    ClickableText(
                        text = AnnotatedString(theme[theme_index]),
                        style = TextStyle(fontSize = 18.sp, color = MaterialTheme.colorScheme.primary,
                        ),
                        onClick = {
                            onThemeOptionSelected(theme_index)
                            viewModel.currentTheme = theme_index
                            AppFunction.putPreferences.run()
                        }
                    )
                }

            }


            Row {
                androidx.compose.material3.Checkbox(
                    checked = checkedStateExitFromApp.value,
                    onCheckedChange = {
                        checkedStateExitFromApp.value = it
                        viewModel.askToExitFromApp = it
                        AppFunction.putPreferences.run()
                    },
                    colors = androidx.compose.material3.CheckboxDefaults.colors(),
                    modifier = Modifier.padding(10.dp),
                )
                ClickableText(
                    text = AnnotatedString(stringResource(R.string.ask_confirmation_to_exit_from_app)),
                    modifier = Modifier.padding(10.dp, 20.dp),
                    style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 18.sp),
                    onClick = { //offset ->
                        checkedStateExitFromApp.value = !checkedStateExitFromApp.value;
                        viewModel.askToExitFromApp = !viewModel.askToExitFromApp
                        AppFunction.putPreferences.run()
                    }
                )
            }

            androidx.compose.material3.Text(
                text = "${stringResource(R.string.favorites)}:",
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            for(contact_index in 0..2) {
                Row () {
                    androidx.compose.material3.Checkbox(
                        checked = checkedStateContacts[contact_index].value,
                        onCheckedChange = {
                            checkedStateContacts[contact_index].value = it
                            viewModel.contactListDetails[contact_index].show.value = checkedStateContacts[contact_index].value

                        },
                        colors = androidx.compose.material3.CheckboxDefaults.colors(),
                        modifier = Modifier.padding(10.dp),
                    )

                    androidx.compose.material3.Icon(
                        painter = painterResource(viewModel.contactListDetails[contact_index].res_id),
                        tint = Color.Unspecified,
                        contentDescription = "ContactLorik",
                        modifier = Modifier
                            .clickable (
                                onClick = {
                                    checkedStateContacts[contact_index].value = !checkedStateContacts[contact_index].value
                                    viewModel.contactListDetails[contact_index].show.value = checkedStateContacts[contact_index].value
                                }
                            )
                            .padding(0.dp, 20.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .size(40.dp),

                        )

                    ClickableText(
                        text = AnnotatedString(viewModel.contactListDetails[contact_index].name),
                        modifier = Modifier.padding(10.dp, 20.dp),
                        style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 18.sp),
                        onClick = {
                            checkedStateContacts[contact_index].value = !checkedStateContacts[contact_index].value
                            viewModel.contactListDetails[contact_index].show.value = checkedStateContacts[contact_index].value
                        }
                    )


                }
            }

        }
    }
}