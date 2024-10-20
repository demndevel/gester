package com.demn.plugincore.operationresult

import android.content.Intent
import android.net.Uri
import java.util.Objects

class IconOperationResult(
    val text: String,
    val intent: Intent? = null,
    val iconUri: Uri,
    override val label: String,
    override val pinToTop: Boolean = false
) : OperationResult {
    override fun hashCode(): Int {
        return Objects.hash(text, label)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IconOperationResult

        if (text != other.text) return false
        if (intent != other.intent) return false
        if (label != other.label) return false
        if (pinToTop != other.pinToTop) return false

        return true
    }
}