package com.demn.plugincore.operationresult

import android.net.Uri
import java.util.UUID

data class CommandOperationResult(
    val uuid: UUID,
    val pluginUuid: UUID,
    val iconUri: Uri,
    val name: String,
) : OperationResult {
    override val type: ResultType = ResultType.Command
}
