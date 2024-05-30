package com.demn.findutil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demn.findutil.di.appModule
import com.demn.findutil.presentation.SearchScreen
import com.demn.findutil.ui.theme.FindUtilTheme
import com.demn.plugins.corePluginsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (GlobalContext.getKoinApplicationOrNull() == null) {
            startKoin {
                androidContext(applicationContext)
                modules(listOf(appModule, corePluginsModule))
            }
        }

        setContent {
            FindUtilTheme {
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            this.finish()
                        }
                ) {
                    SearchScreen(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}