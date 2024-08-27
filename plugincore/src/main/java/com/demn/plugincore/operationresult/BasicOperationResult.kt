package com.demn.plugincore.operationresult

import android.content.Intent
import java.util.Objects

/**
 * Basic operation result
 *
 * @param[text] primary text that will be displayed in the results of the processed input query
 * @param[intent] optional intent that will be launched on
 * @param[type] type of this result for results order. By default, set to the Other
 */
data class BasicOperationResult(
    val text: String,
    val intent: Intent? = null,
    override val type: ResultType = ResultType.Other
) : OperationResult {
    override fun hashCode(): Int {
        return Objects.hash(text, type.ordinal)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BasicOperationResult

        if (text != other.text) return false
        if (intent != other.intent) return false
        if (type != other.type) return false

        return true
    }
}