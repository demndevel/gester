package com.demn.plugincore.operation_result

import android.content.Intent
import kotlinx.parcelize.Parcelize

/**
 * Basic operation result
 *
 * @param[text] primary text that will be displayed in the results of the processed input query
 * @param[description] optional description for the operation result
 */
data class BasicOperationResult(
    val text: String,
    val description: String? = null,
    val intent: Intent? = null
) : OperationResult