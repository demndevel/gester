package com.demn.unit_calc_core_plugin.parse

import com.demn.unit_calc_core_plugin.unit_key.CurrencyUnitKey
import me.y9san9.calkt.units.parse.UnitsParseUnitKeyFunction
import me.y9san9.calkt.units.parse.plus

object ParseCurrency {
    val usd = UnitsParseUnitKeyFunction.ofWords(
        CurrencyUnitKey.Usd,
        "$", "USD", "usd", "dollar", "dollars"
    )

    val rub = UnitsParseUnitKeyFunction.ofWords(
        CurrencyUnitKey.Rub,
        "RUB", "rub", "rouble", "roubles"
    )

    val eur = UnitsParseUnitKeyFunction.ofWords(
        CurrencyUnitKey.Eur,
        "€", "EUR", "eur", "EURO", "euros", "euro"
    )

    val ton = UnitsParseUnitKeyFunction.ofWords(
        CurrencyUnitKey.Ton,
        "TON", "ton"
    )

    val meow = UnitsParseUnitKeyFunction.ofWords(
        CurrencyUnitKey.Meow,
        "MEOW", "meow"
    )

    val function = usd + rub + eur + ton + meow
}