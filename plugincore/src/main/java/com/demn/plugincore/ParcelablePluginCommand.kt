package com.demn.plugincore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
class ParcelablePluginCommand(
    val uuid: UUID,
    val name: String,
    val description: String? = null
) : Parcelable