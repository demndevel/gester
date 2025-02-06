package com.demn.gester.presentation.main.components

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UriIcon(
    iconUri: Uri,
    modifier: Modifier = Modifier
) {
    val placeholderColor = MaterialTheme.colorScheme.primary.toArgb()
    if (!LocalInspectionMode.current) {
        GlideImage(
            model = iconUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        ) {
            it.placeholder(ColorDrawable(placeholderColor))
        }
    } else {
        Box(
            modifier
                .size(32.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}
