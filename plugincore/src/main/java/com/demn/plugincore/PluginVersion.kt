package com.demn.plugincore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PluginVersion(
    val major: Int,
    val minor: Int,
) : Parcelable