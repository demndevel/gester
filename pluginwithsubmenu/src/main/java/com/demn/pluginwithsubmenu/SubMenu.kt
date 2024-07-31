package com.demn.pluginwithsubmenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demn.pluginwithsubmenu.ui.theme.FindUtilTheme

class SubMenu : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindUtilTheme {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(64.dp),
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Submenu of some plugin!",
                            style = MaterialTheme.typography.headlineLarge
                        )

                        Text(
                            text = "lorem ipsum dolor sit amet",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        HorizontalDivider()

                        Text(
                            text = "Choose some option",
                            style = MaterialTheme.typography.headlineLarge
                        )

                        Button(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Option A")
                        }

                        OutlinedButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Option B")
                        }

                        OutlinedButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Option C")
                        }

                        OutlinedButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Option D")
                        }
                    }
                }
            }
        }
    }
}