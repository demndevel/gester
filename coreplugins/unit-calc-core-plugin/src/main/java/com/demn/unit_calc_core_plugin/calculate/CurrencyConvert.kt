package com.demn.unit_calc_core_plugin.calculate

import com.demn.unit_calc_core_plugin.unit_key.CurrencyUnitKey
import me.y9san9.calkt.calculate.CalculateContext
import me.y9san9.calkt.calculate.CalculateResult
import me.y9san9.calkt.math.calculate.MathCalculateSuccess
import me.y9san9.calkt.number.PreciseNumber

object CurrencyConvert {
    operator fun invoke(
        context: CalculateContext,
        value: PreciseNumber,
        from: CurrencyUnitKey,
        to: CurrencyUnitKey
    ): CalculateResult {
        val result = value.times(multiplier(from)).divide(multiplier(to), context.precision)
        return MathCalculateSuccess(result)
    }

    private fun multiplier(
        key: CurrencyUnitKey
    ): PreciseNumber {
        return when (key) {
            CurrencyUnitKey.Rub  -> PreciseNumber.of(1)

            CurrencyUnitKey.Usd -> PreciseNumber.of(89)

            CurrencyUnitKey.Eur -> PreciseNumber.of(98)

            CurrencyUnitKey.Meow -> PreciseNumber.of(228)

            CurrencyUnitKey.Ton -> PreciseNumber.of(586)
        }
    }
}