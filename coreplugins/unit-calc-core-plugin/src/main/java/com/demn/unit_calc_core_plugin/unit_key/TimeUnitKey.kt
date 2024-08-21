package com.demn.unit_calc_core_plugin.unit_key

import me.y9san9.calkt.units.UnitKey
import me.y9san9.calkt.units.annotation.UnitKeySubclass

@OptIn(UnitKeySubclass::class)
sealed interface TimeUnitKey : UnitKey {
    data object Hours : TimeUnitKey
    data object Minutes : TimeUnitKey
    data object Seconds : TimeUnitKey
    data object Millis : TimeUnitKey
}