package com.demn.plugincore.parcelables

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
class ParcelablePluginCommand(
    val uuid: UUID,
    val name: String,
    val iconUri: Uri,
    val description: String? = null
) : Parcelable