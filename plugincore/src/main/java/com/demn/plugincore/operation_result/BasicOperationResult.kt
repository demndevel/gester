package com.demn.plugincore.operation_result

import android.content.Intent

/**
 * Basic operation result
 *
 * @param[text] primary text that will be displayed in the results of the processed input query
 * @param[description] optional description for the operation result
 * @param[priority] priority tag of this result for results order. By default, set to the Other
 */
data class BasicOperationResult(
    val text: String,
    val description: String? = null,
    val intent: Intent? = null,
    val priority: PriorityTag = PriorityTag.Other
) : OperationResult