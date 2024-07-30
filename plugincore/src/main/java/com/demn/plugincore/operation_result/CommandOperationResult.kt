package com.demn.plugincore.operation_result

import java.util.UUID

data class CommandOperationResult(
    val uuid: UUID,
    val name: String,
) : OperationResult {
    override val type: ResultType = ResultType.Command
}
