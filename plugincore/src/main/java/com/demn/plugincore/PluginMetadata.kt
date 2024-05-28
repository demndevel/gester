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
 */
@Parcelize
data class PluginMetadata internal constructor(
    val pluginName: String,
    val description: String? = null,
    val version: String,
    val consumeAnyInput: Boolean = false,
    val commands: List<PluginCommand> = emptyList()
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

fun buildPluginMetadata(pluginName: String, block: PluginMetadataBuilder.() -> Unit) =
    PluginMetadataBuilder().apply(block).build(pluginName)

class PluginMetadataBuilder {
    private val commands = mutableListOf<PluginCommand>()

    var version: String = "0"
    var description: String? = null
    var consumeAnyInput: Boolean = false

    fun command(name: String, triggerRegex: String, block: PluginCommandBuilder.() -> Unit) {
        commands.add(
            PluginCommandBuilder()
                .apply(block)
                .build(name, triggerRegex = triggerRegex)
        )
    }

    fun build(pluginName: String) = PluginMetadata(
        pluginName = pluginName,
        version = version,
        consumeAnyInput = consumeAnyInput,
        commands = commands,
        description = description
    )
}

class PluginCommandBuilder {
    var description: String? = null

    fun build(name: String, triggerRegex: String) = PluginCommand(
        id = UUID.randomUUID(),
        name = name,
        triggerRegex = triggerRegex,
    )
}