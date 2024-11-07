package com.demn.unitcalcplugin.parse

import com.demn.unitcalcplugin.unit_key.CurrencyUnitKey
import me.y9san9.calkt.units.parse.UnitsParseUnitKeyFunction
import me.y9san9.calkt.units.parse.plus

object ParseCurrency {
    val usd = UnitsParseUnitKeyFunction.ofStrings(
        CurrencyUnitKey.Usd,
        "$", "USD", "usd", "dollar", "dollars"
    )

    val rub = UnitsParseUnitKeyFunction.ofStrings(
        CurrencyUnitKey.Rub,
        "RUB", "rub", "rouble", "roubles"
    )

    val eur = UnitsParseUnitKeyFunction.ofStrings(
        CurrencyUnitKey.Eur,
        "€", "EUR", "eur", "EURO", "euros", "euro"
    )

    val ton = UnitsParseUnitKeyFunction.ofStrings(
        CurrencyUnitKey.Ton,
        "TON", "ton"
    )

    val meow = UnitsParseUnitKeyFunction.ofStrings(
        CurrencyUnitKey.Meow,
        "MEOW", "meow"
    )

    val function = usd + rub + eur + ton + meow
}