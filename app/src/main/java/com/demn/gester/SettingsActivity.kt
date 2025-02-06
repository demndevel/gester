package com.demn.gester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.demn.gester.presentation.settings.ui.SettingsScreen
import com.demn.gester.ui.theme.GesterTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GesterTheme {
                SettingsScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                )
            }
        }
    }
}
