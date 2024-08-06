package com.demn.plugincore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class PluginSummary(
    val pluginUuid: UUID,
    val pluginVersion: PluginVersion,
) : Parcelable
