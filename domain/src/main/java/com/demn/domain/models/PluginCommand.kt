package com.demn.domain.models

import android.net.Uri
import java.util.UUID

data class PluginCommand(
    val uuid: UUID,
    val pluginUuid: UUID,
    val iconUri: Uri,
    val name: String,
    val description: String?
)
