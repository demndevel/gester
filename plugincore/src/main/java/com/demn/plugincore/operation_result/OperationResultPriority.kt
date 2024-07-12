package com.demn.plugincore.operation_result

/**
 * Operation result priority
 *
 * This is a simple enum that displays priority of the result for UI. Plugins can associate them with some priorities or, for example, they can make them appear between [Application] or [WebLink]
 */
enum class OperationResultPriority {
    Application,
    WebLink,
    Other
}