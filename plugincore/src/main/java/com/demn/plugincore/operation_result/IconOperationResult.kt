package com.demn.plugincore.operation_result

import android.content.Intent
import android.net.Uri

class IconOperationResult(
    val text: String,
    val intent: Intent? = null,
    val iconUri: Uri,
    override val type: ResultType = ResultType.Other
) : OperationResult