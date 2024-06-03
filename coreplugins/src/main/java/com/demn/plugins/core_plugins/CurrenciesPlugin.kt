package com.demn.plugins.core_plugins

import com.demn.plugincore.buildPluginMetadata
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.TransitionOperationResult
import com.demn.plugins.CorePlugin
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
    version = "0.1"

    command(
        uuid = UUID.fromString("5d94de41-837d-4eb5-b3f9-fc8665c851ff"),
        name = "currency to rub",
        triggerRegex = "(\\d+\\.?\\d*)\\s*(usd|eur|rub)|\\$(\\d+\\.?\\d*)\\s*(usd)"
    ) {
        description = "translate any currency to russian roubles"
    }
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

    val eurMatcher: Matcher = eurPattern.matcher(input.toLowerCase())
    if (eurMatcher.find()) {
        value = eurMatcher.group(1)?.toDoubleOrNull()
        type = CurrencyType.EUR
    }

    val rubMatcher: Matcher = rubPattern.matcher(input.toLowerCase())
    if (rubMatcher.find()) {
        value = rubMatcher.group(1)?.toDoubleOrNull()
        type = CurrencyType.RUB
    }

    if (value != null) {
        return CurrencyValue(value, type)
    }

    return CurrencyValue(0.0, CurrencyType.UNKNOWN)
}

class CurrenciesPlugin : CorePlugin {
    override val metadata = currenciesPluginMetadata

    override fun invokeAnyInput(input: String): List<OperationResult> {
        return emptyList()
    }

    // TODO
    override fun invokePluginCommand(input: String, uuid: UUID): List<OperationResult> {
        if (uuid != metadata.commands.first().id) return emptyList()

        val parsed = parseCurrency(input)

        val transitionResult = TransitionOperationResult(
            initialText = "${parsed.value}${parsed.type}",
            initialDescription = parsed.type.toString(),
            finalText = "${parsed.value * 70}",
            finalDescription = "russian roubles ¢"
        )

        return listOf(transitionResult)
    }
}