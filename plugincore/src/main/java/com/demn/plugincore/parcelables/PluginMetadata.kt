package com.demn.plugincore.parcelables

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
    val pluginId: String,
    val pluginName: String,
    val description: String? = null,
    val version: PluginVersion,
    val consumeAnyInput: Boolean = false,
) : Parcelable

fun buildPluginMetadata(
    pluginId: String,
    pluginName: String,
    block: PluginMetadataBuilder.() -> Unit = {}
) = PluginMetadataBuilder().apply(block).build(pluginId, pluginName)

class PluginMetadataBuilder {
    var version: PluginVersion = PluginVersion(0, 0)
    var description: String? = null
    var consumeAnyInput: Boolean = false

    fun build(pluginId: String, pluginName: String) = PluginMetadata(
        pluginId = pluginId,
        pluginName = pluginName,
        version = version,
        consumeAnyInput = consumeAnyInput,
        description = description
    )
}