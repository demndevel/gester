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
    val commands: List<PluginCommand> = emptyList(),
    val fallbackCommands: List<PluginFallbackCommand> = emptyList()
) : Parcelable

/**
 * Parcelable plugin command
 *
 * @param[id] UUID to identify commands
 * @param[name] name of the command available to users of the app
 * @param[description] nullable description about the command
 * @param[triggerRegex] regular expression that needed to satisfy command needs to be launched
 */
@Parcelize
data class PluginCommand internal constructor(
    val id: UUID,
    val name: String,
    val description: String? = null,
    val triggerRegex: String
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
    private val commands = mutableListOf<PluginCommand>()
    private val fallbackCommands = mutableListOf<PluginFallbackCommand>()

    var version: String = "0"
    var description: String? = null
    var consumeAnyInput: Boolean = false

    fun command(
        uuid: UUID,
        name: String,
        triggerRegex: String,
        block: PluginCommandBuilder.() -> Unit = {}
    ) {
        commands.add(
            PluginCommandBuilder()
                .apply(block)
                .build(uuid = uuid, name, triggerRegex = triggerRegex)
        )
    }

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
        commands = commands,
        fallbackCommands = fallbackCommands,
        description = description
    )
}

class PluginCommandBuilder {
    var description: String? = null

    fun build(uuid: UUID, name: String, triggerRegex: String) = PluginCommand(
        id = uuid,
        name = name,
        triggerRegex = triggerRegex,
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