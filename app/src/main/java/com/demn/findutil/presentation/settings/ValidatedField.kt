package com.demn.findutil.presentation.settings

sealed interface ValidatedField<T> {
    val field: T

    data class Valid<T>(
        override val field: T,
    ) : ValidatedField<T>

    data class Invalid<T>(
        override val field: T,
        val error: SettingValidationError,
    ) : ValidatedField<T>
}

typealias ValidatedStringField = ValidatedField<String>

sealed interface SettingValidationError {
    data object ShouldContainOnlyNumbers : SettingValidationError

    data object ShouldNotBeBlank : SettingValidationError

    data object Other : SettingValidationError
}