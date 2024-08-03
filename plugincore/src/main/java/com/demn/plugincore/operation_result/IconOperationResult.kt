package com.demn.plugincore.operation_result

import android.content.Intent
import android.net.Uri
import java.util.Objects

class IconOperationResult(
    val text: String,
    val intent: Intent? = null,
    val iconUri: Uri,
    override val type: ResultType = ResultType.Other
) : OperationResult {
    override fun hashCode(): Int {
        return Objects.hash(text, type.ordinal)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IconOperationResult

        if (text != other.text) return false
        if (intent != other.intent) return false
        if (type != other.type) return false

        return true
    }
}