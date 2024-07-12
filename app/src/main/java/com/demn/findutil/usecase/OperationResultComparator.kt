package com.demn.findutil.usecase

import com.demn.plugincore.operation_result.BasicOperationResult
import com.demn.plugincore.operation_result.OperationResult
import com.demn.plugincore.operation_result.TransitionOperationResult

class OperationResultComparator : Comparator<OperationResult> {
    override fun compare(o1: OperationResult?, o2: OperationResult?): Int {
        if (o1 == null || o2 == null) return 0

        if (o1 is TransitionOperationResult && o2 !is TransitionOperationResult) return -1

        if (o2 is TransitionOperationResult && o1 !is TransitionOperationResult) return 1

        if (o1 is BasicOperationResult && o2 is BasicOperationResult) {
            if (o1.priority.ordinal > o2.priority.ordinal) return -1
            if (o1.priority.ordinal < o2.priority.ordinal) return 1
        }

        return 0
    }
}