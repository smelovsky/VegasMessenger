package com.example.vegas

import androidx.compose.runtime.Composable
import com.example.vegas.screens.ChatScreen
import com.example.vegas.screens.ChatScreenState
import com.example.vegas.screens.HomeScreen
import com.example.vegas.screens.SettingsScreen

data class ScreenParams(
    val permissionsGranted: Boolean = false,
    val INTERNET: Boolean = false,
    val ACCESS_NETWORK_STATE: Boolean = false,
    val state: ChatScreenState,
    )
typealias ComposableFun = @Composable (screenParams: ScreenParams) -> Unit

sealed class TabItem(var icon: Int, var title: Int, var screen: ComposableFun) {
    object Home : TabItem(R.drawable.ic_label, R.string.tab_name_home, { HomeScreen() } )
    object Chat : TabItem(R.drawable.ic_label, R.string.tab_name_chat, { ChatScreen(it.state) } )
    object Settings : TabItem(R.drawable.ic_label, R.string.tab_name_settings, { SettingsScreen() } )
}
