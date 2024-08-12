package com.demn.plugincore.util

import android.os.ParcelUuid
import java.util.*

fun UUID.toParcelUuid(): ParcelUuid = ParcelUuid(this)