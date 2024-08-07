package com.demn.plugincore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Parcelable plugin metadata
 *
 * @param[pluginName] plugin name
 * @param[description] can be null
 * @param[version] version of the plugin
 * @param[consumeAnyInput] this means that the plugin can accept any input without specific regexp. Example: app search plugin that looks for plugins just by typing the text
 */
@Parcelize
data class PluginMetadata internal constructor(
    val pluginUuid: UUID,
    val pluginName: String,
    val description: String? = null,
    val version: PluginVersion,
    val consumeAnyInput: Boolean = false,
) : Parcelable

fun buildPluginMetadata(
    pluginUuid: UUID,
    pluginName: String,
    block: PluginMetadataBuilder.() -> Unit = {}
) = PluginMetadataBuilder().apply(block).build(pluginUuid, pluginName)

class PluginMetadataBuilder {
    var version: PluginVersion = PluginVersion(0, 0)
    var description: String? = null
    var consumeAnyInput: Boolean = false

    fun build(pluginUuid: UUID, pluginName: String) = PluginMetadata(
        pluginUuid = pluginUuid,
        pluginName = pluginName,
        version = version,
        consumeAnyInput = consumeAnyInput,
        description = description
    )
}