package com.demn.unit_calc_core_plugin.parse

import com.demn.unit_calc_core_plugin.unit_key.TimeUnitKey
import me.y9san9.calkt.units.parse.UnitsParseUnitKeyFunction
import me.y9san9.calkt.units.parse.plus

object ParseTime {
    val hours = UnitsParseUnitKeyFunction.ofWords(
        TimeUnitKey.Hours,
        "h", "hr", "hrs", "hour", "hours"
    )
    val minutes = UnitsParseUnitKeyFunction.ofWords(
        TimeUnitKey.Minutes,
        "min", "mins", "minute", "minutes"
    )
    val seconds = UnitsParseUnitKeyFunction.ofWords(
        TimeUnitKey.Seconds,
        "sec", "second", "seconds"
    )
    val milliseconds = UnitsParseUnitKeyFunction.ofWords(
        TimeUnitKey.Millis,
        "millis", "millisecond", "milliseconds"
    )

    val function = hours + minutes + seconds + minutes + milliseconds
}