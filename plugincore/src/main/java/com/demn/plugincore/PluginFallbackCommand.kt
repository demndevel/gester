package com.demn.plugincore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Parcelable plugin fallback command
 *
 * @param[uuid] UUID to identify fallback command
 * @param[name] display name of the fallback command
 * @param[description] nullable description about the command
 */
@Parcelize
data class ParcelablePluginFallbackCommand(
    val uuid: UUID,
    val name: String,
    val description: String? = null
) : Parcelable