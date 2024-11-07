package com.demn.unitcalcplugin.parse

import me.y9san9.calkt.parse.ParseContext
import me.y9san9.calkt.units.UnitKey
import me.y9san9.calkt.units.parse.UnitsParseUnitKeyFunction
import me.y9san9.calkt.units.parse.plus

object DefaultParseUnitKey : UnitsParseUnitKeyFunction {
    val function = ParseDistance.function + ParseTime.function + ParseCurrency.function

    override fun invoke(context: ParseContext): UnitKey {
        return function(context)
    }
}
