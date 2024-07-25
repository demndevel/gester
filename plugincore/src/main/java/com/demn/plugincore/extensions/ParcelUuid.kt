package com.demn.plugincore.extensions

import android.os.ParcelUuid
import java.util.*

fun UUID.toParcelUuid(): ParcelUuid = ParcelUuid(this)