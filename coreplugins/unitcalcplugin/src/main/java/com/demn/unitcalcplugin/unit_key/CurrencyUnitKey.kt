package com.demn.unitcalcplugin.unit_key

import me.y9san9.calkt.units.UnitKey
import me.y9san9.calkt.units.annotation.UnitKeySubclass

@OptIn(UnitKeySubclass::class)
sealed interface CurrencyUnitKey : UnitKey {
    data object Usd : CurrencyUnitKey
    data object Rub : CurrencyUnitKey
    data object Eur : CurrencyUnitKey
    data object Ton : CurrencyUnitKey
    data object Meow : CurrencyUnitKey
}