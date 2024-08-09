package com.demn.applications_core_plugin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ApplicationDbo(
    @PrimaryKey
    val packageName: String,
    val name: String,
    val iconUri: String
)