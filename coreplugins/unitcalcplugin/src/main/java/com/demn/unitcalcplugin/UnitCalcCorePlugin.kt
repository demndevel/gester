package com.demn.unitcalcplugin

import me.y9san9.calkt.Expression

//class UnitCalcCorePlugin() : CorePlugin {
//    companion object {
//        const val LOG_TAG = "UnitCalcCorePlugin"
//    }
//
//    override val metadata: PluginMetadata = buildPluginMetadata(
//        pluginId = "com.demn.unitcalc",
//        pluginName = "Unit calc plugin",
//    ) {
//        version = PluginVersion(0, 0)
//        consumeAnyInput = true
//    }
//
//    override suspend fun invokeCommand(uuid: UUID) = Unit
//
//    override suspend fun getPluginCommands(): List<PluginCommand> = emptyList()
//
//    override suspend fun getPluginFallbackCommands(): List<PluginFallbackCommand> = emptyList()
//
//    override suspend fun getPluginSettings(): List<PluginSetting> = emptyList()
//
//    override suspend fun invokeAnyInput(input: String): List<OperationResult> {
//        Log.i(LOG_TAG, "Plugin initialized")
//
//        val parseResult = tryParse(input) { context ->
//            context.parseUnitsExpression(DefaultParseUnitKey)
//        }
//
//        if (parseResult !is ParseResult.Success) {
//            Log.e(LOG_TAG, "An error occurred during parsing")
//
//            return emptyList()
//        }
//
//        val expression = parseResult.value
//        val result = tryCalculate(expression, precision = 12) { context ->
//            context.calculateUnitsExpression(DefaultUnitsConvert)
//        }
//
//        return when (result) {
//            is MathCalculateSuccess -> {
//                listOf(
//                    TransitionOperationResult(
//                        initialText = expression.toPrettyString(),
//                        initialDescription = "Math expression",
//                        finalText = result.number.toString(),
//                        finalDescription = "Result"
//                    )
//                )
//            }
//
//            is UnitsCalculateSuccess -> {
//                listOf(
//                    TransitionOperationResult(
//                        initialText = expression.toPrettyString(),
//                        initialDescription = "Units expression",
//                        finalText = "${result.number} ${result.key}",
//                        finalDescription = "Result"
//                    )
//                )
//            }
//
//            else -> {
//                Log.e(LOG_TAG, "An error occurred during calculation")
//
//                emptyList()
//            }
//        }
//    }
//
//    override suspend fun invokePluginFallbackCommand(input: String, uuid: UUID) = Unit
//}

private fun Expression.toPrettyString(): String {
    return this.toString()
        .replace("plus", "+")
        .replace("minus", "-")
        .replace("div", "/")
        .replace("times", "*")
        .replace(".convert", " to ")
}