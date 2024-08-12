package com.demn.aidl;

import com.demn.plugincore.parcelables.ParcelableOperationResult;
import com.demn.plugincore.parcelables.PluginMetadata;
import com.demn.plugincore.parcelables.PluginSetting;
import com.demn.plugincore.parcelables.PluginSummary;
import com.demn.plugincore.parcelables.ParcelablePluginCommand;
import com.demn.plugincore.parcelables.ParcelablePluginFallbackCommand;
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