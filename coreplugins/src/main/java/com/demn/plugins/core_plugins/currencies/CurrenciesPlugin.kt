package com.demn.plugins.core_plugins.currencies

import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.plugincore.PluginSetting
import com.demn.plugincore.PluginSettingType
import com.demn.plugincore.PluginVersion
import com.demn.plugincore.buildPluginMetadata
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.TransitionOperationResult
import com.demn.plugins.CorePlugin
import com.demn.plugins.CorePluginsSettingsRepository
import java.util.Locale
import java.util.UUID
import java.util.regex.Matcher
import java.util.regex.Pattern

val currenciesPluginMetadata = buildPluginMetadata(
    pluginUuid = UUID.fromString("77b6d4d8-0fc8-4bb5-98f2-e3bbacf2218f"),
    pluginName = "currencies"
) {
    consumeAnyInput = false
    description = ""
    version = PluginVersion(0, 0)
}

internal enum class CurrencyType {
    USD,
    EUR,
    RUB,
    UNKNOWN
}

internal data class CurrencyValue(val value: Double, val type: CurrencyType)

private fun parseCurrency(input: String): CurrencyValue {
    val usdPattern = Pattern.compile("(\\d+\\.?\\d*)\\s*usd|\\$(\\d+\\.?\\d*)")
    val eurPattern = Pattern.compile("(\\d+\\.?\\d*)\\s*eur")
    val rubPattern = Pattern.compile("(\\d+\\.?\\d*)\\s*rub")

    var value: Double? = null
    var type: CurrencyType = CurrencyType.UNKNOWN

    val usdMatcher: Matcher = usdPattern.matcher(input.lowercase(Locale.getDefault()))
    if (usdMatcher.find()) {
        value = usdMatcher.group(1)?.toDoubleOrNull() ?: usdMatcher.group(2)?.toDoubleOrNull()
        type = CurrencyType.USD
    }

    val eurMatcher: Matcher = eurPattern.matcher(input.lowercase())
    if (eurMatcher.find()) {
        value = eurMatcher.group(1)?.toDoubleOrNull()
        type = CurrencyType.EUR
    }

    val rubMatcher: Matcher = rubPattern.matcher(input.lowercase())
    if (rubMatcher.find()) {
        value = rubMatcher.group(1)?.toDoubleOrNull()
        type = CurrencyType.RUB
    }

    if (value != null) {
        return CurrencyValue(value, type)
    }

    return CurrencyValue(0.0, CurrencyType.UNKNOWN)
}

class CurrenciesPlugin(
    private val corePluginsSettingsRepository: CorePluginsSettingsRepository
) : CorePlugin {
    override val metadata = currenciesPluginMetadata

    override fun invokeCommand(uuid: UUID) {
        TODO("Not yet implemented")
    }

    override fun getPluginCommands(): List<PluginCommand> = emptyList()

    override fun getPluginFallbackCommands(): List<PluginFallbackCommand> = emptyList()

    private val usdCostSettingUuid = UUID.fromString("2bf52834-430b-4ec4-bade-ad6eb563c4ed")

    override fun getPluginSettings(): List<PluginSetting> {
        return listOf(
            PluginSetting(
                pluginUuid = metadata.pluginUuid,
                pluginSettingUuid = usdCostSettingUuid,
                settingName = "How much USD should cost in RUB?",
                settingDescription = "",
                settingValue = "100",
                settingType = PluginSettingType.String
            )
        )
    }

    override fun invokeAnyInput(input: String): List<OperationResult> {
        return emptyList()
    }

    override fun invokePluginFallbackCommand(input: String, uuid: UUID) = Unit
}