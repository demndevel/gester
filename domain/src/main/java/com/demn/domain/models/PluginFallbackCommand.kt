package com.demn.domain.models

import android.net.Uri
import java.util.*

data class PluginFallbackCommand(
    val uuid: UUID,
    val pluginUuid: UUID,
    val name: String,
    val iconUri: Uri,
    val description: String? = null
)