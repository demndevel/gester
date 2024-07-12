package com.demn.aidl;

import com.demn.plugincore.ParcelableOperationResult;
import com.demn.plugincore.PluginMetadata;
import com.demn.plugincore.PluginSetting;
import android.os.ParcelUuid;

interface IOperation {
    List<ParcelableOperationResult> executeCommand(in String commandUuid, in String input);

    List<ParcelableOperationResult> executeAnyInput(in String input);

    PluginMetadata fetchPluginData();

    void setSetting(in ParcelUuid settingUuid, in String newValue);

    List<PluginSetting> getPluginSettings();
}