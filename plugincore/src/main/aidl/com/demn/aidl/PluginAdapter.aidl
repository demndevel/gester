package com.demn.aidl;

import com.demn.plugincore.ParcelableOperationResult;
import com.demn.plugincore.PluginMetadata;
import com.demn.plugincore.PluginSetting;
import com.demn.plugincore.PluginSummary;
import com.demn.plugincore.ParcelablePluginCommand;
import com.demn.plugincore.ParcelablePluginFallbackCommand;
import android.os.ParcelUuid;

interface PluginAdapter {
    void executeFallbackCommand(in ParcelUuid commandUuid, in String input);

    void executeCommand(in ParcelUuid commandUuid);

    List<ParcelableOperationResult> executeAnyInput(in String input);

    List<ParcelablePluginCommand> getAllCommands();

    List<ParcelablePluginFallbackCommand> getAllFallbackCommands();

    PluginMetadata getPluginMetadata();

    PluginSummary getPluginSummary();

    void setSetting(in ParcelUuid settingUuid, in String newValue);

    List<PluginSetting> getPluginSettings();
}