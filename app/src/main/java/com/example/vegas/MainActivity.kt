package com.example.vegas

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vegas.screens.BottomBarChat
import com.example.vegas.screens.BottomBarHome
import com.example.vegas.screens.ChatScreenState
import com.example.vegas.ui.theme.VegasTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


val tabs = listOf(
    TabItem.Home,
    TabItem.Chat,
    TabItem.Settings,
)

sealed class AppFunction(var run: () -> Unit) {

    object putPreferences : AppFunction( {} )
}

val basePermissions = arrayOf(
    Manifest.permission.INTERNET,
    Manifest.permission.ACCESS_NETWORK_STATE,
    )


lateinit var viewModel: VegasViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    lateinit var INTERNET: MutableState<Boolean>
    lateinit var ACCESS_NETWORK_STATE: MutableState<Boolean>

    lateinit var prefs: SharedPreferences
    val APP_PREFERENCES_THEME = "theme"
    val APP_PREFERENCES_ASK_TO_EXIT_FROM_APP = "ask_to_exit_from_app"

    lateinit var theme: MutableState<Boolean>
    lateinit var permissionsGranted: MutableState<Boolean>

    var isAppInited: Boolean = false
    var isFistStart: Boolean = true

    @OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        AppFunction.putPreferences.run = ::putPreferences

        setContent {

            INTERNET = remember { mutableStateOf(false) }
            ACCESS_NETWORK_STATE = remember { mutableStateOf(false) }
            permissionsGranted = remember { mutableStateOf(hasAllPermissions()) }

            viewModel = hiltViewModel()

            getPreferences()

            theme = remember { mutableStateOf(viewModel.currentTheme == 1) }


            isAppInited = true

            if (isFistStart) {
                if (permissionsGranted.value) {

                    viewModel.contactReadAllItems()

                    isFistStart = false
                }
            }

            VegasTheme(theme.value) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var pagerState: PagerState = rememberPagerState(0)

                    androidx.compose.material3.Scaffold(
                        topBar = {
                            TopAppBar(
                                backgroundColor = MaterialTheme.colorScheme.background,
                                title = {
                                    Row() {
                                        Text(
                                            text = stringResource(R.string.app_name),
                                        )
                                    }

                                },
                                modifier = Modifier.height(30.dp),
                                actions = {
                                    androidx.compose.material3.IconButton(onClick = {
                                        exitFromApp()
                                    }) {
                                        androidx.compose.material3.Icon(
                                            imageVector = Icons.Filled.ExitToApp,
                                            contentDescription = "Exit",
                                        )
                                    }
                                },
                            )
                        },
                        bottomBar = {
                            BottomAppBar(
                                modifier = Modifier.height(60.dp),
                                backgroundColor = MaterialTheme.colorScheme.background,
                            )
                            {
                                when (tabs[pagerState.currentPage]) {
                                    TabItem.Home -> BottomBarHome()
                                    TabItem.Chat -> BottomBarChat()
                                }
                            }
                        }
                    ) { padding ->

                        Column(modifier = Modifier.padding(padding)) {
                            Tabs(tabs = tabs, pagerState = pagerState)
                            TabsContent(
                                tabs = tabs, pagerState = pagerState,
                                permissionsGranted.value,
                                INTERNET.value,
                                ACCESS_NETWORK_STATE.value,
                                viewModel.state.value,
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun Tabs(tabs: List<TabItem>, pagerState: PagerState) {
        val scope = rememberCoroutineScope()
        ScrollableTabRow(
            backgroundColor = MaterialTheme.colorScheme.background,
            selectedTabIndex = pagerState.currentPage,
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    //icon = { Icon(painter = painterResource(id = tab.icon), contentDescription = "") },
                    text = { Text(stringResource(tab.title)) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun TabsContent(
        tabs: List<TabItem>, pagerState: PagerState,
        permissionsGranted: Boolean,
        INTERNET: Boolean,
        ACCESS_NETWORK_STATE: Boolean,
        state: ChatScreenState,
    ) {
        HorizontalPager(state = pagerState, count = tabs.size) { page ->

            var screenParams: ScreenParams = ScreenParams(
                permissionsGranted,
                INTERNET,
                ACCESS_NETWORK_STATE,
                state,
            )

            tabs[page].screen(screenParams)

        }
    }

    override fun onBackPressed() {

        if (viewModel.askToExitFromApp) {

            val alertDialog = android.app.AlertDialog.Builder(this)

            alertDialog.apply {
                setIcon(R.drawable.vegas_11)
                setTitle(getApplicationContext().getResources().getString(R.string.app_name))
                setMessage(getApplicationContext().getResources().getString(R.string.do_you_really_want_to_close_the_application))
                setPositiveButton(getApplicationContext().getResources().getString(R.string.yes))
                { _: DialogInterface?, _: Int -> exitFromApp() }
                setNegativeButton(getApplicationContext().getResources().getString(R.string.no))
                { _, _ -> }

            }.create().show()
        }
        else {
            exitFromApp()
        }

    }


    fun exitFromApp() {
        onBackPressedDispatcher.onBackPressed()
    }

    fun hasAllPermissions(): Boolean{
        var result = true

        if (!hasBasePermissions()) {
            result = false
        }

        return result
    }

    fun hasBasePermissions(): Boolean{
        var result = true
        basePermissions.forEach {

            val permission = ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            if ( !permission)
            {
                Log.d("zzz","PERMISSION DENIED ${it}")
                result = false
            } else {
                Log.d("zzz","PERMISSION GRANTED ${it}")
            }
            when (it) {
                Manifest.permission.INTERNET -> INTERNET.value = permission
                Manifest.permission.ACCESS_NETWORK_STATE -> ACCESS_NETWORK_STATE.value = permission
            }
        }
        return result
    }

    fun putPreferences() {
        val editor = prefs.edit()
        editor.putInt(APP_PREFERENCES_THEME, viewModel.currentTheme).apply()
        editor.putBoolean(APP_PREFERENCES_ASK_TO_EXIT_FROM_APP, viewModel.askToExitFromApp).apply()

        theme.value = (viewModel.currentTheme == 1)
    }

    fun getPreferences() {
        if(prefs.contains(APP_PREFERENCES_THEME)){
            viewModel.currentTheme = prefs.getInt(APP_PREFERENCES_THEME, 0)
        }
        if(prefs.contains(APP_PREFERENCES_ASK_TO_EXIT_FROM_APP)){
            viewModel.askToExitFromApp = prefs.getBoolean(APP_PREFERENCES_ASK_TO_EXIT_FROM_APP, true)
        }
    }

}

