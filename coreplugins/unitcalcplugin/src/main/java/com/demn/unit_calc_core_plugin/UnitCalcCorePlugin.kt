package com.demn.unit_calc_core_plugin

import android.util.Log
import com.demn.domain.models.PluginCommand
import com.demn.domain.models.PluginFallbackCommand
import com.demn.plugincore.operationresult.OperationResult
import com.demn.plugincore.operationresult.TransitionOperationResult
import com.demn.plugincore.parcelables.PluginMetadata
import com.demn.plugincore.parcelables.PluginSetting
import com.demn.plugincore.parcelables.PluginVersion
import com.demn.plugincore.parcelables.buildPluginMetadata
import com.demn.coreplugins.base.CorePlugin
import com.demn.unit_calc_core_plugin.calculate.DefaultUnitsConvert
import com.demn.unit_calc_core_plugin.parse.DefaultParseUnitKey
import me.y9san9.calkt.Expression
import me.y9san9.calkt.calculate.tryCalculate
import me.y9san9.calkt.math.calculate.MathCalculateSuccess
import me.y9san9.calkt.parse.ParseResult
import me.y9san9.calkt.parse.tryParse
import me.y9san9.calkt.units.calculate.UnitsCalculateSuccess
import me.y9san9.calkt.units.calculate.calculateUnitsExpression
import me.y9san9.calkt.units.parse.parseUnitsExpression
import java.util.*

class UnitCalcCorePlugin() : CorePlugin {
    companion object {
        const val LOG_TAG = "UnitCalcCorePlugin"
    }

    override val metadata: PluginMetadata = buildPluginMetadata(
        pluginId = "com.demn.unitcalc",
        pluginName = "Unit calc plugin",
    ) {
        version = PluginVersion(0, 0)
        consumeAnyInput = true
    }

    override suspend fun invokeCommand(uuid: UUID) = Unit

    override suspend fun getPluginCommands(): List<PluginCommand> = emptyList()

    override suspend fun getPluginFallbackCommands(): List<PluginFallbackCommand> = emptyList()

    override suspend fun getPluginSettings(): List<PluginSetting> = emptyList()

    override suspend fun invokeAnyInput(input: String): List<OperationResult> {
        Log.i(LOG_TAG, "Plugin initialized")

        val parseResult = tryParse(input) { context ->
            context.parseUnitsExpression(DefaultParseUnitKey)
        }

        if (parseResult !is ParseResult.Success) {
            Log.e(LOG_TAG, "An error occurred during parsing")

            return emptyList()
        }

        val expression = parseResult.value
        val result = tryCalculate(expression, precision = 12) { context ->
            context.calculateUnitsExpression(DefaultUnitsConvert)
        }

        return when (result) {
            is MathCalculateSuccess -> {
                listOf(
                    TransitionOperationResult(
                        initialText = expression.toPrettyString(),
                        initialDescription = "Math expression",
                        finalText = result.number.toString(),
                        finalDescription = "Result"
                    )
                )
            }

            is UnitsCalculateSuccess -> {
                listOf(
                    TransitionOperationResult(
                        initialText = expression.toPrettyString(),
                        initialDescription = "Units expression",
                        finalText = "${result.number} ${result.key}",
                        finalDescription = "Result"
                    )
                )
            }

            else -> {
                Log.e(LOG_TAG, "An error occurred during calculation")

                emptyList()
            }
        }
    }

    override suspend fun invokePluginFallbackCommand(input: String, uuid: UUID) = Unit
}

private fun Expression.toPrettyString(): String {
    return this.toString()
        .replace("plus", "+")
        .replace("minus", "-")
        .replace("div", "/")
        .replace("times", "*")
        .replace(".convert", " to ")
}