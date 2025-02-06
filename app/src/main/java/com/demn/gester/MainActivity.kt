package com.demn.gester

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
import com.demn.appsearchplugin.appSearchingPluginMetadata
import com.demn.appsearchplugin.syncAppsCacheCommandUuid
import com.demn.domain.pluginmanagement.PluginRepository
import com.demn.gester.presentation.main.SearchScreen
import com.demn.gester.ui.theme.GesterTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val pluginRepository: PluginRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                pluginRepository.invokeCommand(
                    commandUuid = syncAppsCacheCommandUuid,
                    pluginId = appSearchingPluginMetadata.pluginId
                )
            }
        }

        setContent {
            GesterTheme {
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
