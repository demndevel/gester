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
 * @param[commands] command list of [PluginCommand]
 * @param[fallbackCommands] fallback command list of [PluginFallbackCommand]
 */
@Parcelize
data class PluginMetadata internal constructor(
    val pluginUuid: UUID,
    val pluginName: String,
    val description: String? = null,
    val version: String,
    val consumeAnyInput: Boolean = false,
    val fallbackCommands: List<PluginFallbackCommand> = emptyList()
) : Parcelable

/**
 * Parcelable plugin fallback command
 *
 * @param[id] UUID to identify fallback command
 * @param[name] display name of the fallback command
 * @param[description] nullable description about the command
 */
@Parcelize
data class PluginFallbackCommand internal constructor(
    val id: UUID,
    val name: String,
    val description: String? = null
) : Parcelable

fun buildPluginMetadata(
    pluginUuid: UUID,
    pluginName: String,
    block: PluginMetadataBuilder.() -> Unit = {}
) = PluginMetadataBuilder().apply(block).build(pluginUuid, pluginName)

class PluginMetadataBuilder {
    private val fallbackCommands = mutableListOf<PluginFallbackCommand>()

    var version: String = "0"
    var description: String? = null
    var consumeAnyInput: Boolean = false

    fun fallbackCommand(
        uuid: UUID,
        name: String,
        block: PluginFallbackCommandBuilder.() -> Unit = {}
    ) {
        fallbackCommands.add(
            PluginFallbackCommandBuilder()
                .apply(block)
                .build(uuid = uuid, name = name)
        )
    }

    fun build(pluginUuid: UUID, pluginName: String) = PluginMetadata(
        pluginUuid = pluginUuid,
        pluginName = pluginName,
        version = version,
        consumeAnyInput = consumeAnyInput,
        fallbackCommands = fallbackCommands,
        description = description
    )
}

class PluginFallbackCommandBuilder {
    var description: String? = null

    fun build(uuid: UUID, name: String) = PluginFallbackCommand(
        id = uuid,
        name = name,
        description = description
    )
}