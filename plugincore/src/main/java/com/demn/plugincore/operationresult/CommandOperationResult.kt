package com.demn.plugincore.operationresult

import android.net.Uri
import java.util.UUID

data class CommandOperationResult(
    val uuid: UUID,
    val pluginId: String,
    val iconUri: Uri,
    val name: String,
) : OperationResult {
    override val label: String = "Command"
    override val pinToTop: Boolean = false
}
