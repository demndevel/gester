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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.demn.applications_core_plugin.appSearchingMetadata
import com.demn.applications_core_plugin.syncAppsCacheCommandUuid
import com.demn.findutil.di.appModule
import com.demn.findutil.di.coreplugins.appSearchingPluginModule
import com.demn.findutil.di.dataModule
import com.demn.findutil.di.domainModule
import com.demn.findutil.di.pluginManagementModule
import com.demn.findutil.presentation.main.SearchScreen
import com.demn.findutil.ui.theme.FindUtilTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (GlobalContext.getKoinApplicationOrNull() == null) {
            startKoin {
                androidContext(applicationContext)
                modules(
                    listOf(
                        appModule,
                        appSearchingPluginModule,
                        dataModule,
                        pluginManagementModule,
                        domainModule
                    )
                )
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
//                corePluginsProvider.invokePluginCommand( TODO
//                    commandUuid = syncAppsCacheCommandUuid,
//                    pluginId = appSearchingMetadata.pluginId
//                )
            }
        }

        setContent {
            FindUtilTheme {
                val interactionSource = remember { MutableInteractionSource() }
                val context = LocalContext.current

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