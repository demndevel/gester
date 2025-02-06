package com.demn.unitcalcplugin

import android.util.Log
import com.demn.unitcalcplugin.calculate.DefaultUnitsConvert
import com.demn.unitcalcplugin.parse.DefaultParseUnitKey
import io.github.demndevel.gester.core.operationresult.OperationResult
import io.github.demndevel.gester.core.parcelables.PluginMetadata
import io.github.demndevel.gester.core.parcelables.PluginVersion
import io.github.demndevel.gester.core.parcelables.buildPluginMetadata
import io.github.demndevel.gester.core.dsl.PluginService
import io.github.demndevel.gester.core.operationresult.TransitionOperationResult
import me.y9san9.calkt.Expression
import me.y9san9.calkt.calculate.tryCalculate
import me.y9san9.calkt.math.calculate.MathCalculateSuccess
import me.y9san9.calkt.parse.ParseResult
import me.y9san9.calkt.parse.tryParse
import me.y9san9.calkt.units.calculate.UnitsCalculateSuccess
import me.y9san9.calkt.units.calculate.calculateUnitsExpression
import me.y9san9.calkt.units.parse.parseUnitsExpression

val unitCalcMetadata: PluginMetadata = buildPluginMetadata(
    pluginId = "com.demn.unitcalc",
    pluginName = "Unit calc plugin",
) {
    version = PluginVersion(0, 0)
    consumeAnyInput = true
}

class UnitCalcPlugin : PluginService(unitCalcMetadata) {
    companion object {
        const val LOG_TAG = "UnitCalcCorePlugin"
    }

    override fun executeAnyInputHandler(input: String): List<OperationResult> {
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
}

private fun Expression.toPrettyString(): String {
    return this.toString()
        .replace("plus", "+")
        .replace("minus", "-")
        .replace("div", "/")
        .replace("times", "*")
        .replace(".convert", " to ")
}
