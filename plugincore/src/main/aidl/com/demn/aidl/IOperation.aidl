package com.demn.aidl;

import com.demn.plugincore.ParcelableOperationResult;
import com.demn.plugincore.PluginMetadata;

interface IOperation {
    List<ParcelableOperationResult> executeCommand(in String commandUuid, in String input);

    List<ParcelableOperationResult> executeAnyInput(in String input);

    PluginMetadata fetchPluginData();
}